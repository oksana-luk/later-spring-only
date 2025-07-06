package ru.practicum.item;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import ru.practicum.exceptions.ItemRetrieverException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


@Slf4j
@Service
public class UrlMetadataRetrieverImpl implements UrlMetadataRetriever {
    private final HttpClient client;

    // В качестве параметра конструктора, сервис принимает количество секунд в течении которых
    // он будет ожидать ответа от сервера обрабатывающего url-адрес, переданный в качестве
    // ссылки для сохранения. Этот параметр Spring получает из файла настроек и автоматически
    // внедряет в бин. Если в файле настроек таймаут не указан, то по умолчанию он будет равен
    // 120 секундам.
    UrlMetadataRetrieverImpl(@Value("${url-metadata-retriever.read_timeout-sec:120}") int readTimeout) {
        // Для получения метаданных об URL воспользуемся стандартным HttpClient'ом.
        // Для этого создадим его экземпляр с нужными нам настройками
        // Во первых, указываем всегда переходить по новому адресу, если сервер
        // обрабатывающий URL указывает нам на это. Такая ситуация может возникнуть,
        // например если пользователь сохраняет сокращенную ссылку (полученную, например
        // через сервис bitly.com) или по каким-либо другим причинам. Также указываем таймаут
        // ожидания соединения.
        this.client =  HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    @Override
    public UrlMetadata retrieve(String urlString) {
        // - Выполняем запрос к переданному url методом HEAD и получаем заголовки.
        // - - для начала преобразуем строку с адресом в объект класса URI
        final URI uri;

        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            //Если адрес не соответствует правилам URI адресов, то генерируем исключение.
            throw new ItemRetrieverException("The URL is malformed: " + urlString, e);
        }

        // - - получаем информацию при помощи метода HEAD, он позволит получить часть информацию
        // - - без необходимости загружать контент
        HttpResponse<Void> response = connect(uri, "HEAD", HttpResponse.BodyHandlers.discarding());

        // - - Анализируем заголовки, полученного ответа, чтобы понять,
        // - - какой тип контента возвращает сервер по данному адресу
        String contentType = response.headers().
                firstValue(HttpHeaders.CONTENT_TYPE)
                .orElse("*");

        // - - Для удобства, воспользуемся вспомогательным классом из состава
        // - - Spring и преобразуем полученную строку в объект MediaType
        MediaType mediaType = MediaType.parseMediaType(contentType);

        // - На основе полученных заголовков определяем тип контента: текст, изображение или видео.
        final UrlMetadataImpl result;

        if (mediaType.isCompatibleWith(MimeType.valueOf("text/*"))) {
            result = handleText(response.uri());
        } else if (mediaType.isCompatibleWith(MimeType.valueOf("image/*"))) {
            result = handleImage(response.uri());
        } else if (mediaType.isCompatibleWith(MimeType.valueOf("video/*"))) {
            result = handleVideo(response.uri());
        } else {
            throw new ItemRetrieverException("The content type [" + mediaType
                    + "] at the specified URL is not supported.");
        }

        // Заканчиваем формирование метаданных. Сохраняем изначальную ссылку
        // как normalUrl, а конечную ссылку как resolvedUrl. Если было
        // перенаправление, то эти ссылки будут отличаться, в обратном случае
        // они будут совпадать. Также сохраняем тип содержимого и дату, когда
        // ссылка была обработана.
        return result.toBuilder()
                .normalUrl(urlString)
                .resolvedUrl(response.uri().toString())
                .mimeType(mediaType.getType())
                .dateResolved(Instant.now())
                .build();
    }

    // Вспомогательный метод для соединения с сервером и получения информации о сохраняемом url-адресе
    private <T> HttpResponse<T> connect(URI url, String method, HttpResponse.BodyHandler<T> responseBodeHandler) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();
        final HttpResponse<T> response;

        try {
            response = client.send(request, responseBodeHandler);
        } catch (IOException e) {
            throw new ItemRetrieverException("Cannot retrieve data from the URL: " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cannot get the metadata for url: " + url
                    + " because the thread was interrupted.", e);
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());

        if (status == null) {
            throw new ItemRetrieverException("The server returned an unknown status code: " + response.statusCode());
        }
        if (status.equals(HttpStatus.UNAUTHORIZED) || status.equals(HttpStatus.FORBIDDEN)) {
            throw new ItemRetrieverException("There is no access to the resource at the specified URL: " + url);
        }
        if (status.isError()) {
            throw new ItemRetrieverException("Cannot get the data on the item because the server returned an error."
                    + "Response status: " + status);
        }

        return response;
    }

    // Вспомогательный метод для получения метаданных о содержимом типа text
    // и его подтипов, это в том числе html и т.д.
    private UrlMetadataImpl handleText(URI url) {
        // Отправим get-запрос чтобы получить содержимое
        HttpResponse response = connect(url, "GET", HttpResponse.BodyHandlers.ofString());

        // воспользуемся библиотекой Jsoup для парсинга содержимого
        Document doc = Jsoup.parse((String) response.body());

        // ищем в полученном документе html-тэги, говорящие, что он
        // содержит видео или аудио информацию
        Elements imgElements = doc.getElementsByTag("img");
        Elements videoElements = doc.getElementsByTag("video");

        // добавляем полученные данные в ответ. В том числе находим заголовок
        // полученной страницы.
        return UrlMetadataImpl.builder()
                .title(doc.title())
                .hasImage(!imgElements.isEmpty())
                .hasVideo(!videoElements.isEmpty())
                .build();
    }

    // Вспомогательный метод для получения метаданных о содержимом типа video
    private UrlMetadataImpl handleVideo(URI url) {
        String name = new File(url).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasVideo(true)
                .build();
    }

    // Вспомогательный метод для получения метаданных о содержимом типа image
    private UrlMetadataImpl handleImage(URI url) {
        String name = new File(url).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasImage(true)
                .build();
    }

    @lombok.Value
    @Builder(toBuilder = true)
    static class UrlMetadataImpl implements UrlMetadata {
        String normalUrl;
        String resolvedUrl;
        String mimeType;
        String title;
        boolean hasImage;
        boolean hasVideo;
        Instant dateResolved;
    }
}

