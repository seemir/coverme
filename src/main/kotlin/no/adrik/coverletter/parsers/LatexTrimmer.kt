package no.adrik.coverletter.parsers

import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Author: Samir Adrik
 * Email:  samir.adrik@gmail.com
 */

class LatexTrimmer(
    finnCode: String,
    private var latexString: String,
    private var searchEngine: String = "finn.no"
) {

    private val resourcesPath: String = "${java.nio.file.Paths.get("").toAbsolutePath()}/src/main/resources/"
    private val preamble: String = "${readFromResources("preamble.txt")}\n\n"
    private var finnRef = "\\href{${readFromResources("finn-url.txt") + finnCode}}{$searchEngine}"

    private fun readFromResources(fileName: String): String {
        val inputStream: InputStream = File("${resourcesPath}$fileName").inputStream()
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun today(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MMMM.yyyy")
        return now.format(formatter)
    }

    fun removeLatexNotation(): String {
        return latexString.replace(preamble, "").replace("\\noindent", "")
//               Spacing, date and generic tags
            .replace("\\begin", "").replace("\\end", "")
            .replace("\\hspace", "").replace("\\vspace", "")
            .replace("(\\today)\\\\[0,5cm]", today()).replace("*{-0.75cm}", "")
            .replace("*{-0,3cm}", "").replace("*{-0,4cm}", "")
            .replace("*{-0,6cm}", "").replace("*{-0,2cm}", "")
            .replace("[0,1cm]", "").replace("[0,2cm]", "")
            .replace("[0,5cm]", "").replace("[0,8cm]", "")
            .replace("[1,1cm]", "").replace("[1,5cm]", "")
            .replace("p{2cm}p{13cm}", "").replace("{document}", "")
            .replace("\\head", "").replace("\\endhead", "")
            .replace("simple_text", "")
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
    }
}