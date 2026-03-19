package com.ckstjr.erroranalysis.slack;

import com.slack.api.Slack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Component
public class SlackNotifier {

    @Value("${slack.webhook-url}")
    private String webhookUrl;

    public void send(String text) throws IOException {
        Slack.getInstance().send(webhookUrl, payload(p -> p.text(text)));
    }
}
