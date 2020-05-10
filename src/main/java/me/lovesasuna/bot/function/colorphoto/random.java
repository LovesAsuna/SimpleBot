package me.lovesasuna.bot.function.colorphoto;

import com.fasterxml.jackson.databind.JsonNode;
import kotlin.Pair;
import me.lovesasuna.bot.Main;
import me.lovesasuna.bot.util.NetWorkUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author LovesAsuna
 * @date 2020/4/19 14:06
 */

public class random implements Source{

    @Override
    public String fetchData() {
        String source = "http://api.mtyqx.cn/api/random.php?return=json";
        Pair<InputStream,Integer> result = NetWorkUtil.fetch(source);
        try {
            JsonNode root = Main.Companion.getMapper().readTree(result.getFirst());
            return root.get("imgurl").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
