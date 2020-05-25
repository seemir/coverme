package no.adrik.coverletter

import org.jsoup.Jsoup

import java.io.File
import java.net.URL
import java.nio.file.Paths


/**
 * Author: Samir Adrik
 * Email:  samir.adrik@gmail.com
 */

class AdInfoParser(private val finnCode: String) {

    private val resourcesPath: String
    private val finnBaseUrl: String
    private val finnUrl: String

    init {
        val finnCodeRegex = "^1[0-9]{7,8}\$".toRegex()
        require(finnCode.matches(finnCodeRegex)) {
            "'$finnCode' is an invalid finnCode"
        }

        this.resourcesPath = "${Paths.get("").toAbsolutePath()}/src/main/resources/"
        this.finnBaseUrl = File("${resourcesPath}finn-url.txt").inputStream().bufferedReader()
            .use { it.readText() }
        this.finnUrl = finnBaseUrl + finnCode
    }

    private fun response(): String {
        return URL(finnUrl).readText()
    }

    fun parseFinnAd(): Map<String, String> {
        val document = Jsoup.parse(response())
        val positionInfo = document.getElementsByAttributeValue("class", "definition-list")
        val companyInfo = document.getElementsByAttributeValue("class", "definition-list definition-list--inline")

        val titles = positionInfo.select("dt").plus(companyInfo.select("dt")[2]).map { it.ownText() }
        val values = positionInfo.select("dd").plus(companyInfo.select("dd")[2]).map { it.ownText() }
        return titles.zip(values).toMap()
    }
}