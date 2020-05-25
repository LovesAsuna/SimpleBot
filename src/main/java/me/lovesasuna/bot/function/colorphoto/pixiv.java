package me.lovesasuna.bot.function.colorphoto;

import com.fasterxml.jackson.databind.JsonNode;
import kotlin.Pair;
import me.lovesasuna.bot.Main;
import me.lovesasuna.bot.data.BotData;
import me.lovesasuna.bot.util.NetWorkUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:06
 */
public class pixiv implements Source{
    @Override
    public String fetchData() {
        String source = "https://api.lolicon.app/setu/?apikey=560424975e992113ed5977";
        Pair<InputStream,Integer> result = NetWorkUtil.fetch(source);
        try {
            InputStream inputStream = result.getFirst();
            JsonNode root = BotData.INSTANCE.getObjectMapper().readTree(inputStream);
            return root.get("data").get(0).get("url").asText() + "|" + root.get("quota").asText();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
