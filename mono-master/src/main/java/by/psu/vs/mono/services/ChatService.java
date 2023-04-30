package by.psu.vs.mono.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ChatService {

    private Map<String, String> chatMap = new LinkedHashMap<>();

    public synchronized void saveMessage(String text) {
        chatMap.put(Instant.now().toString(), text);
    }

    public synchronized String getChatMap() {
        StringBuilder sb = new StringBuilder();

        chatMap.forEach((k,v) -> {
//            sb.append("<br />")/*.append(k).append(":")*/.append(v);
            sb.append("<div>")/*.append(k).append(":")*/.append(v).append("</div>");


        });

        return sb.toString();
    }
}
