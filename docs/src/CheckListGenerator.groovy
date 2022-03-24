/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
import java.nio.file.Files
import java.nio.file.Path
import java.text.MessageFormat

/** Allows to generate all lists with checks
 *
 * @author Dmitriy Marmyshev
 *
 */

/**
 * The description of the check.
 */
class CheckDescription {
    String title
    String checkId
    Path path
    String url
}

/**
 * Read check description.
 *
 * @param file the markdown file to get header and file name as check ID
 * @return the check description
 */
CheckDescription readCheckDescription(String file) {
    def fileReader = new File(file)
    println("Read Markdown file: " + fileReader.absolutePath)
    def line
    def descr = null
    fileReader.withReader { r ->
        while(  ( line = r.readLine() ) != null ) {
            if (line.stripLeading().startsWith("# ")) {
                def header = line.strip().substring(1).strip()
                descr = new CheckDescription();
                descr.title = header;
                descr.path = fileReader.toPath();
                def checkId = fileReader.name;
                if(checkId.endsWith(".md"))
                    checkId = checkId.substring(0, checkId.length() -3)
                descr.checkId = checkId;
                break;
            }
        } ;
    }
    return descr;
}

/**
 * Read descriptions in markdown files from directory.
 *
 * @param dirPath the directory path
 * @return the list of check descriptions
 */
List<CheckDescription> readDescriptions(String dirPath) {
    def checks = new ArrayList();
    def dir = new File(dirPath);
    println("Read directory: " + dir.absolutePath)
    dir.eachFile { file ->
        if(file.exists() && file.name.endsWith(".md")) {
            def descr = readCheckDescription(file.absolutePath)
            if(descr != null) {
                checks.add(descr);
            }
        }
    }

    return checks;
}

/**
 * Update check list by file path.
 *
 * @param listFile the list file
 * @param checks the check descriptions to update
 */
void updateCheckList(Path listFile, List<CheckDescription> checks, String desciption = "") {

    def file = listFile.toFile()
    def line
    def header = "";
    file.withReader { r ->
        while(  ( line = r.readLine() ) != null ) {
            if (line.stripLeading().startsWith("# ")) {
                header = line
                break;
            }
        } ;
    }
    Collections.sort(checks, { o1, o2 -> o1.checkId.compareTo(o2.checkId)})

    println("Write file: " + file.absolutePath)
    file.withWriter { w ->
        w.writeLine(header)
        w.writeLine("")
        w.writeLine("")
        if(!desciption.blank) {
            w.write(desciption)
            w.writeLine("")
            w.writeLine("")
        }
        w.writeLine("| Код проверки | Наименование |")
        w.writeLine("|--------------|--------------|")
        checks.each { check ->
            def checkIdContent = "[" +  check.checkId+ "]("  + check.url+ ")"
            line = "| " + checkIdContent + " | " + check.title + " |"
            w.writeLine(line)
        }
    }
}

String getCheckStatistic(Map<String, Integer> checkSegments, List<CheckDescription> checks) {
    StringBuilder sb = new StringBuilder();
    sb.append "Общее количество проверок 1С:Стандарты разработки V8: "
    sb.append checks.size()
    sb.append System.lineSeparator()
    sb.append System.lineSeparator()
    checkSegments.each { segment, total ->
        sb.append "- "
        sb.append segment
        sb.append ": "
        sb.append total
        sb.append System.lineSeparator()
    }
    sb.append System.lineSeparator()
    return sb.toString();
}

void generateToc(List<CheckDescription> checks, String segment, Path tocDir) {
    Files.createDirectories(tocDir)
    def file = tocDir.resolve(segment + ".xml").toFile();

    def begin = "<toc label=\"{0}\" link_to=\"toc.xml#{0}\">"
    def end = "</toc>"
    def topicTemplate = "   <topic href=\"../com.e1c.v8codestyle.{0}/check.descriptions/ru/{1}.html\" label=\"{2}\" />"

    Collections.sort(checks, { o1, o2 -> o1.title.compareTo(o2.title)})

    file.withWriter { w ->
        w.write('''<?xml version="1.0" encoding="UTF-8"?>
<?NLS TYPE="org.eclipse.help.toc"?>
''')
        w.writeLine(MessageFormat.format(begin, segment))
        checks.each { check ->
            def title = check.title
            title = title.replace("&", "&amp;")
            title = title.replace("\"", "&quot;")
            title = title.replace("<", "&lt;")
            title = title.replace(">", "&gt;")
            title = title.replace("'", "&apos;")
            w.writeLine(MessageFormat.format(topicTemplate, segment, check.checkId, title))
        }
        w.writeLine(end)
    }
}

void addExternalLink(List<CheckDescription> checks, Path basePath) {
    def webUrlPreffix = "https://github.com/1C-Company/v8-code-style/tree/master/bundles/"

    def editPattern ='''
<div style="text-align: right">
<a href="{0}" target="_blank" title="Редактировать описание">
<svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
<path d="M20.71 7.04c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.37-.39-1.02-.39-1.41 0l-1.84 1.83 3.75 3.75M3 17.25V21h3.75L17.81 9.93l-3.75-3.75L3 17.25z">
</path></svg> Редактировать описание</a></div>
'''
    checks.each { check ->
        def path = basePath.relativize(check.path)
        def url = webUrlPreffix + path.toString()
        def editText = MessageFormat.format(editPattern, url)
        def file = check.path.toFile()
        def lines = file.readLines();
        def editTextAdded = false
        file.withWriter { w ->

            lines.each { line ->
                w.writeLine(line)
                if(!editTextAdded && line.startsWith("# ")) {
                    w.writeLine(editText)
                    editTextAdded = true
                }
            }
        }
    }
}

void generateUrls(List<CheckDescription> checks, boolean helpContentLinks, Path basePath, String segment) {

    // go upper from /html/checks/check_index.html
    def topicTemplate = "../../../com.e1c.v8codestyle.{0}/check.descriptions/ru/{1}.html"

    checks.each { check ->
        if(helpContentLinks) {
            check.url = MessageFormat.format(topicTemplate, segment, check.checkId)
        }
        else {
            def path = basePath.relativize(check.path)
            check.url = path.toString()
        }
    }
}

println "Get base path from script property \"basePath\"..."
def basePath = properties.get("basePath") as String

if(basePath == null || basePath.blank) {
    println "Script property \"basePath\" is not set."
}
if((basePath == null || basePath.blank) && this.binding.variables.containsKey("args") && this.args.size() > 0) {
    println "Get \"basePath\" from first calling parameter..."
    basePath = this.args[0] as String;
}

if(basePath == null || basePath.blank) {
    println "Base path is not set. Exit..."
    return
}

// allows to copy check descriptions to make local refs in Eclipse help, generate other help content
def generateHelpContent = Boolean.valueOf(properties.get("generateHelpContent") as String)

println "Current base path: " + basePath
println("-----------------------------")

println("Generate check index content")
println("-----------------------------")

// generate check lists for: md, form, module, ql, right

def checksDir = "checks"
def htmlDir = "html"
def listBaseDir = Path.of(basePath, checksDir)
def descrDirPattern = Path.of(basePath, "/../bundles/com.e1c.v8codestyle.{0}/markdown/ru")
def checkIdFile = listBaseDir.resolve("checks_index.txt")
def tocDir = Path.of(basePath, htmlDir, checksDir)

def webUrlRemovePreffix = Path.of(basePath, "/../bundles")


def int basePathSegments = (new File(basePath)).toPath().nameCount

def Map<String, Integer> checkSegments = new HashMap();
checkSegments.put("md", 0)
checkSegments.put("form", 0)
checkSegments.put("bsl", 0)
checkSegments.put("ql", 0)
checkSegments.put("right", 0)

def List<CheckDescription> allChecks = new ArrayList<>();
checkSegments.forEach({ segment, total ->

    println("Generate check index for: " + segment + "...")
    def dirPath = MessageFormat.format(descrDirPattern.toString(), segment)
    def checks = readDescriptions(dirPath);
    if(generateHelpContent) {
        generateToc(checks, segment, tocDir)
    }

    generateUrls(checks, generateHelpContent, listBaseDir, segment)
    def description = "Общее количество проверок: " + checks.size()
    def listFile = listBaseDir.resolve(segment + ".md")
    updateCheckList(listFile, checks, description)
    allChecks.addAll(checks)
    println("Done.")
    checkSegments.put(segment, checks.size())
})

println("-----------------------------")
println("Generate all check index...")
def description = getCheckStatistic(checkSegments, allChecks)
def listFile = listBaseDir.resolve("check_index.md")
updateCheckList(listFile, allChecks, description)
println("Done.")

// TODO Adding "Edit content" button should be added to md-files before bundle building
//if(generateHelpContent) {
//    println("-----------------------------")
//    println("Add external GitHub link...")
//    addExternalLink(allChecks, webUrlRemovePreffix)
//    println("Done.")
//}

println("-----------------------------")
println("Update all check ID index...")
def List<String> checkIds = new ArrayList<>();
allChecks.each { check ->
    checkIds.add(check.checkId)
}
Collections.sort(checkIds)
def file = checkIdFile.toFile()
println("Write file: " + file.absolutePath)
file.withWriter { w ->
    checkIds.each { checkId ->
        w.writeLine(checkId)
    }
}
println("Done.")
