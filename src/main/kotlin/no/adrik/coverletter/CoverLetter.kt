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

    private fun today(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MMMM.yyyy")
        return now.format(formatter)
    }

    private fun createDateStamp(): String {
        val today = today()
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
        return "${baseIntro.replace("companyName", companyName)
            .replace("position", position.toLowerCase())
            .replace("field", field).replace("finnRef", finnRef)
            .replace("suckUpLine", suckUpLine)}\n\n"
    }

    private fun createEnding(): String {
        val baseEnding = readFromResources("ending.txt")
        return "${baseEnding.replace("position", position.toLowerCase())
            .replace("companyName", companyName)}\n\n"
    }

    fun createCoverLetter(latex: Boolean = true, writeToFile: Boolean = true, includeCv: Boolean = true) {
        var rawCoverLetter = "$preamble${createHeader()}${createDateStamp()}${createTitle()}" +
                "${createIntro()}$education$experience${createEnding()}"

        val endDocument = "${readFromResources("end-document.txt")}\n\n"
        val cv = "${readFromResources("cv.txt")}\n\n"

        rawCoverLetter = if (includeCv) {
            "${rawCoverLetter}$cv"
        } else {
            "${rawCoverLetter}$endDocument"
        }

        val fileContent: String
        val postfix: String

        if (!latex) {
            fileContent = rawCoverLetter.replace(preamble, "").replace("\\noindent", "")
//               Spacing, date and generic tags
                .replace("\\begin", "").replace("\\end", "")
                .replace("\\hspace", "").replace("\\vspace", "")
                .replace("(\\today)\\\\[0,5cm]", today()).replace("*{-0.75cm}", "")
                .replace("*{-0,3cm}", "").replace("*{-0,4cm}", "")
                .replace("*{-0,6cm}", "").replace("*{-0,2cm}", "")
                .replace("[0,1cm]", "").replace("[0,2cm]", "")
                .replace("[0,5cm]", "").replace("[1,1cm]", "")
                .replace("[0,8cm]", "").replace("p{2cm}p{13cm}", "")
                .replace("{document}", "").replace("\\head", "")
                .replace("\\endhead", "").replace("simple_text", "")
//               Font tags
                .replace("\\newpage", "").replace("\\textcopyright", "")
                .replace("\\centering", "").replace("flushright", "")
                .replace("{centering}", "").replace("\\textwidth", "")
                .replace("\\texttt", "").replace("\\footnotesize", "")
                .replace("\\normalsize", "").replace("\\Large", "")
                .replace("\\textbf", "").replace("\\small", "")
                .replace("small", "").replace("\\textit", "")
//               Table tags
                .replace("tabularx", "").replace("tabular", "")
                .replace("{table}", "").replace("{lX}", "")
                .replace("&", "").replace("[h!]", "")
                .replace("{ll}", "")
//               Reference tags
                .replace("\\href{https://nmbu.brage.unit.no/nmbu-xmlui/handle/11250/", "")
                .replace("2580610", "").replace("2403557", "")
                .replace("\\href{https://github.com/seemir}{", "").replace(finnRef, searchEngine)
                .replace("\\href{https://www.codewars.com/users/seemir}{", "")
//               Escape characters
                .replace("{", "").replace("}", "").replace("\\", "")
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
    coverLetter.createCoverLetter()
}