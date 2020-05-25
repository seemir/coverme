package no.adrik.coverletter

/**
 * Author: Samir Adrik
 * Email:  samir.adrik@gmail.com
 */

import java.io.File
import java.io.InputStream
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CoverLetter(
    private var finnCode: String,
    private var searchEngine: String = "finn.no",
    private var position: String = "",
    private var field: String = "statistikk og analyse",
    private var suckUpLine: String = "ett av Norges ledende selskap"
) {
    private var adParser: AdInfoParser = AdInfoParser(finnCode)
    private val resourcesPath: String = "${Paths.get("").toAbsolutePath()}/src/main/resources/"
    private var finnRef = "\\href{${readFromResources("finn-url.txt") + finnCode}}{$searchEngine}"

    private var adInfo: Map<String, String> = adParser.parseFinnAd()
    private var companyName: String = adInfo["Arbeidsgiver"].toString()
    private var companyAddress: String = adInfo["Sted"].toString()
        .replace(", ", ",\\\\ \n\\noindent\\simple_text") + "\\\\"
    private var deadLine: String = adInfo["Frist"].toString()

    private val preamble: String = "${readFromResources("preamble.txt")}\n\n"
    private val education: String = "${readFromResources("education.txt")}\n\n"
    private val experience: String = "${readFromResources("experience.txt")}\n\n"

    init {
        if (position == "") {
            this.position = adInfo["Stillingstittel"].toString()
        }
    }

    private fun readFromResources(fileName: String): String {
        val inputStream: InputStream = File("${resourcesPath}$fileName").inputStream()
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun createHeader(): String {
        val baseHeader = readFromResources("header.txt")
        return "${baseHeader.replace("companyName", companyName)
            .replace("companyAddress", companyAddress).replace("deadLine", deadLine)
            .replace("finnCode", finnCode)}\n\n"
    }

    private fun createDateStamp(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MMMM.yyyy")
        val today = now.format(formatter)
        val baseDate = readFromResources("date-stamp.txt")
        return "${baseDate.replace("today", today)}\n\n"
    }

    private fun createTitle(): String {
        val baseTitle = readFromResources("title.txt")
        return baseTitle.replace("position", position.toUpperCase())
            .replace("companyName", companyName.toUpperCase())
    }

    private fun createIntro(): String {
        val baseIntro = readFromResources("intro.txt")
        return "${baseIntro.replace("companyName", companyName).replace("position", position)
            .replace("field", field).replace("finnRef", finnRef)
            .replace("suckUpLine", suckUpLine)}\n\n"
    }

    private fun createEnding(): String {
        val baseEnding = readFromResources("ending.txt")
        return "${baseEnding.replace("position", position).replace("companyName", companyName)}\n\n"
    }

    fun createCoverLetter(latex: Boolean = true, writeToFile: Boolean = false) {
        val rawCoverLetter = "$preamble${createHeader()}${createDateStamp()}${createTitle()}" +
                "${createIntro()}$education$experience${createEnding()}"

        val fileContent: String
        val postfix: String

        if (!latex) {
            fileContent = rawCoverLetter.replace(preamble, "").replace("\\noindent", "")
                .replace("\\simple_text", "").replace("\\hspace*{-0.75cm}", "")
                .replace("&", "").replace("\\begin{tabular}{ l l }", "")
                .replace("\\end{tabular}", "").replace("\\textbf{", "")
                .replace("\\end{document}", "").replace("\\begin{flushright}", "")
                .replace("\\end{flushright}", "").replace(finnRef, searchEngine)
                .replace("}", "").replace("\\", "")
            postfix = "plain.txt"
        } else {
            fileContent = rawCoverLetter.replace("\\simple_text", " ")
            postfix = "latex.tex"
        }

        if (writeToFile) {
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val today = now.format(formatter).replace(".", "_")
                .replace(":", "").replace(" ", "_")

            val coverLetterPath = "$resourcesPath/cover-letters/"

            File(coverLetterPath).deleteRecursively()
            File(coverLetterPath).mkdir()

            val fileName =
                "${coverLetterPath}søknad_${companyName.toLowerCase()}_${position.toLowerCase()
                    .replace(" ", "_").replace(",", "")}_${today}_$postfix"
            File(fileName).bufferedWriter().use { out -> out.write(fileContent) }
        } else {
            println(fileContent)
        }
    }
}

fun main() {
    val coverLetter = CoverLetter("finnCode", suckUpLine = "suckUpLine")
    coverLetter.createCoverLetter(latex = true, writeToFile = true)
}