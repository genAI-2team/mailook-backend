package com.mailook.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // 성향, 새벽 배치. 이 모델은 temperature 1만 받는다. reasoning effort none은 넣지 않는다.
    @Bean
    public ChatClient preferenceChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(
                        ChatOptions.builder()
                                .model("gpt-6.2-sol")
                                .temperature(1.0)
                                .build()
                )
                .build();
    }

    // 옷 분류. reasoning effort가 none이 아니어도, 기본값 0.7이 나가지 않게 temperature는 1만 둔다.
    @Bean
    public ChatClient classifyChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-6.1-terra")
                                .temperature(1.0)
                                .reasoningEffort("low")
                                .build()
                )
                .build();
    }

    // 추천. 옷 분류와 같은 옵션.
    @Bean
    public ChatClient recommendChatClient(ChatClient.Builder builder) {
        return builder
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-6.1-terra")
                                .temperature(1.0)
                                .reasoningEffort("low")
                                .build()
                )
                .build();
    }

}
