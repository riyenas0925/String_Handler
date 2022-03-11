package dev.riyenas.assignment.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest
public class ProcessorTest {

    @Autowired
    private ResourceLoader resourceLoader;

    private Document document;

    @BeforeEach
    void setUp() throws IOException {
        Resource resource = resourceLoader.getResource("crawler_test_web_page_input.html");
        document = Jsoup.parse(resource.getFile(), "UTF-8", "", Parser.xmlParser());
    }

    @Test
    @DisplayName("노출 유형이 HTML일 경우 HTML코드에서 태그를 제거한다.")
    void HTMLTypeRemoveTag() {
        // given
        Processor processor = new HTMLTypeProcessor(document);

        // when
        String expected = processor.getData(document);

        // that
        String actual = "Page Title My First Heading My Password 243590-812345-823714-902341-21340";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("노출 유형이 TEXT 일 경우에는 HTML코드에서 모든 텍스트 포함한다.")
    void TextTypeRemoveTag() {
        // given
        Processor processor = new TextTypeProcessor(document);

        // when
        String expected = processor.getData(document);

        // that
        String actual = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>My First Heading</h1>\n" +
                "<p>My Password 243590-812345-823714-902341-21340</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        assertThat(actual).isEqualToIgnoringWhitespace(expected);
    }

    @Test
    @DisplayName("결과 데이터는 영어와 숫자로만 구성된다")
    void ResultDataOnlyEnglishAndNumber() throws IOException {
        // given
        Processor processor = new TextTypeProcessor(document);

        String data = "12345 ~!@#$%^&*()_+ abcde \n\n ABCDE";

        // when
        String expected = processor.extractEnglishAndNumber(data);

        // that
        String actual = "12345abcdeABCDE";
        assertThat(actual).isEqualToIgnoringWhitespace(expected);
    }

}