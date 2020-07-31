package me.lovesasuna.bot.util.PictureSearchUtil

import me.lovesasuna.bot.util.interfaces.PictureSearchSource
import org.jsoup.Jsoup

object Ascii2d : PictureSearchSource {
    private val ascii2d = "https://ascii2d.net/search/url/"
    override fun search(url: String): List<Result> {
        val request = Jsoup.connect("$ascii2d$url")
        request.followRedirects(true)
        val html = request.get()
        val elements = html.body().getElementsByClass("container")
        val sources = elements.select("div.row.item-box")[1]
        val thumbnail = "https://ascii2d.net" + sources.select("img[loading=lazy]").attr("src")
        val resultList = ArrayList<Result>()
        val extUrlsList = ArrayList<String>()
        sources.select("a[target]").apply {
            for (i in 0 until this.size) {
                extUrlsList.add(this[i].attr("href"))
            }
        }
        resultList.add(Result(-1, thumbnail, extUrlsList, "Ascii2d不显示"))
        return resultList
    }
}