package com.hyosakura.bot.controller.picture.search

import org.jsoup.Jsoup

object Ascii2d : SearchSource<PictureResult> {
    private val ascii2d = "https://ascii2d.net/search/url/"
    override suspend fun search(url: String): List<PictureResult> {
        val request = Jsoup.connect("$ascii2d$url")
        request.followRedirects(true)
        val html = request.get()
        val elements = html.body().getElementsByClass("container")
        val pictureResultList = ArrayList<PictureResult>()
        for (i in 1..2) {
            val sources = elements.select("div.row.item-box")[i]
            val thumbnail = "https://ascii2d.net" + sources.select("img[loading=lazy]").attr("src")
            val extUrlsList = ArrayList<String>()
            sources.select("a[target]").apply {
                for (j in 0 until this.size) {
                    extUrlsList.add(this[j].attr("href"))
                }
            }
            pictureResultList.add(PictureResult(-1.0, thumbnail, extUrlsList, "Ascii2d不显示"))
        }

        return pictureResultList
    }
}