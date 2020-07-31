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
        val resultList = ArrayList<Result>()
        for (i in 1..2) {
            val sources = elements.select("div.row.item-box")[i]
            val thumbnail = "https://ascii2d.net" + sources.select("img[loading=lazy]").attr("src")
            val extUrlsList = ArrayList<String>()
            sources.select("a[target]").apply {
                for (j in 0 until this.size) {
                    extUrlsList.add(this[j].attr("href"))
                }
            }
            resultList.add(Result(-1, thumbnail, extUrlsList, "Ascii2d不显示"))
        }

        return resultList
    }
}