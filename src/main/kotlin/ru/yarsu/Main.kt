package ru.yarsu

import io.pebbletemplates.pebble.PebbleEngine
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.DayOfWeek
import kotlin.io.path.writer

object Main {
    private const val OUTPUT_DIRECTORY = "site"

    @JvmStatic
    fun main(args: Array<String>) {
        val scheduleInfo = formSchedule()
        val pebbleEngine = configureEngine()
        // Создаём целевые каталоги
        Files.createDirectories(Paths.get(OUTPUT_DIRECTORY, "courses"))

        // Помещаем данные в шаблон "some.peb" и выводим на экран
        //generateDataFromSomePeb(scheduleInfo, pebbleEngine)
        generateDataFromCoursesPeb(scheduleInfo, pebbleEngine)
        var i = 0
        while(i<scheduleInfo.courses.size) {
            generateDataFromCoursePeb(scheduleInfo, pebbleEngine, i)
            i++
        }
        generateDataFromSchedulePeb(scheduleInfo, pebbleEngine)
    }


    private fun generateDataFromCoursesPeb(
        scheduleInfo: ScheduleInfo,
        pebbleEngine: PebbleEngine
    ) {
        val context = mutableMapOf<String, Any>()
        val items = scheduleInfo.courses
        context["courses"] = items
        printTemplateToFile(pebbleEngine, "course-list.peb", "course-list.html", context)
    }

    private fun generateDataFromCoursePeb(
        scheduleInfo: ScheduleInfo,
        pebbleEngine: PebbleEngine,
        i: Int
    ) {
        val context = mutableMapOf<String, Any>()
        val item = scheduleInfo.courses[i]
        context["course"] = item

        val sch = scheduleInfo.scheduleItems
        context["scheduleItems"] = sch

        context["Monday"] = DayOfWeek.MONDAY
        context["Tuesday"] = DayOfWeek.TUESDAY
        context["Wednesday"] = DayOfWeek.WEDNESDAY
        context["Thursday"] = DayOfWeek.THURSDAY
        context["Friday"] = DayOfWeek.FRIDAY
        context["Saturday"] = DayOfWeek.SATURDAY

        printTemplateToFile(pebbleEngine, "course.peb", "courses/" + item.id + ".html", context)
    }

    private fun generateDataFromSchedulePeb(
        scheduleInfo: ScheduleInfo,
        pebbleEngine: PebbleEngine
    ) {
        val context = mutableMapOf<String, Any>()

        val sch = scheduleInfo.scheduleItems
        context["scheduleItems"] = sch

        val courses = scheduleInfo.courses
        context["courses"] = courses

        context["Monday"] = DayOfWeek.MONDAY
        context["Tuesday"] = DayOfWeek.TUESDAY
        context["Wednesday"] = DayOfWeek.WEDNESDAY
        context["Thursday"] = DayOfWeek.THURSDAY
        context["Friday"] = DayOfWeek.FRIDAY
        context["Saturday"] = DayOfWeek.SATURDAY

        printTemplateToFile(pebbleEngine, "schedule.peb", "schedule.html", context)
    }

    private fun configureEngine(): PebbleEngine {
        return PebbleEngine.Builder().build()
    }

    private fun printTemplateToFile(
        pebbleEngine: PebbleEngine,
        templateName: String,
        fileName: String,
        context: Map<String, Any>,
    ) {
        val template = pebbleEngine.getTemplate("ru/yarsu/${templateName}")
        val fileWriter = Paths.get(OUTPUT_DIRECTORY, fileName).writer()
        try {
            template.evaluate(fileWriter, context)
        } catch (exception: IOException) {
            System.err.apply {
                println("Не могу обработать шаблон $templateName")
                println(exception.message)
            }
        }
    }
}