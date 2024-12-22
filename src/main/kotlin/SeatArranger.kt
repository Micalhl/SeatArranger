import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * SeatArranger
 * .StandardConfigs
 *
 * @author mical
 * @since 2024/12/21 12:48
 */
object SeatArranger {

    // 座位表
    // X to Y to 行 to 列
    private val seats = mapOf(
        (1 to 6) to (3 to 0),
        (1 to 5) to (5 to 0),
        (1 to 4) to (7 to 0),
        (1 to 3) to (9 to 0),
        (1 to 2) to (11 to 0),
        (1 to 1) to (13 to 0), // 第一列
        (2 to 6) to (3 to 2),
        (2 to 5) to (5 to 2),
        (2 to 4) to (7 to 2),
        (2 to 3) to (9 to 2),
        (2 to 2) to (11 to 2),
        (2 to 1) to (13 to 2), // 第二列
        (3 to 6) to (3 to 3),
        (3 to 5) to (5 to 3),
        (3 to 4) to (7 to 3),
        (3 to 3) to (9 to 3),
        (3 to 2) to (11 to 3),
        (3 to 1) to (13 to 3), // 第三列
        (4 to 6) to (3 to 5),
        (4 to 5) to (5 to 5),
        (4 to 4) to (7 to 5),
        (4 to 3) to (9 to 5),
        (4 to 2) to (11 to 5),
        (4 to 1) to (13 to 5), // 第四列
        (5 to 6) to (3 to 7),
        (5 to 5) to (5 to 7),
        (5 to 4) to (7 to 7),
        (5 to 3) to (9 to 7),
        (5 to 2) to (11 to 7),
        (5 to 1) to (13 to 7), // 第五列
        (6 to 6) to (3 to 8),
        (6 to 5) to (5 to 8),
        (6 to 4) to (7 to 8),
        (6 to 3) to (9 to 8),
        (6 to 2) to (11 to 8),
        (6 to 1) to (13 to 8), // 第六列
        (7 to 6) to (3 to 10),
        (7 to 5) to (5 to 10),
        (7 to 4) to (7 to 10),
        (7 to 3) to (9 to 10),
        (7 to 2) to (11 to 10),
        (7 to 1) to (13 to 10) // 第七列
    )

    // 四个护法
    private val specialSeats = mapOf(
        1 to (1 to 6), 2 to (1 to 4), 3 to (25 to 3), 4 to (25 to 7)
    )

    private val ignored = hashSetOf<String>()

    fun doMoveSeats(sheet: XSSFSheet) {
        doMoveX(sheet)
        doMoveY(sheet)
    }

    fun doMoveSpecial(sheet: XSSFSheet) {
        val prepareSeatsSpecial = hashMapOf<String, Pair<Int, Int>>()
        for (specialId in specialSeats.keys) {
            val (row, cell) = specialSeats[specialId] ?: continue
            val block = sheet.getRow(row).getCell(cell)
            val name = block.toString()
            var newId = specialId + 1
            if (newId > 4) newId = 1
            val newLocation = specialSeats[newId] ?: continue
            prepareSeatsSpecial += name to newLocation
        }
        for (name in prepareSeatsSpecial.keys) {
            val (row, cell) = prepareSeatsSpecial[name] ?: continue
            val block = sheet.getRow(row).getCell(cell)
            block.setCellValue(name)
        }
    }

    fun doMoveX(sheet: XSSFSheet) {
        val prepareSeatsX = hashMapOf<String, Pair<Int, Int>>()
        for (loc1 in seats.keys) {
            val loc2 = seats[loc1] ?: continue
            val (x, y) = loc1 // 标注坐标
            val (row, cell) = loc2 // 真实坐标
            val block = sheet.getRow(row).getCell(cell)
            val name = block?.toString() ?: continue
            var newX = x - 1 // 向左
            if (newX < 1) newX = 7
            val targetLoc1 = seats[newX to y] ?: continue
            prepareSeatsX += name to targetLoc1
        }
        for (name in prepareSeatsX.keys) {
            val (row, cell) = prepareSeatsX[name] ?: continue
            val block = sheet.getRow(row).getCell(cell)
            block.setCellValue(name)
        }
    }

    fun doMoveY(sheet: XSSFSheet) {
        val prepareSeatsY = hashMapOf<String, Pair<Int, Int>>()
        for (loc1 in seats.keys) {
            val loc2 = seats[loc1] ?: continue
            val (x, y) = loc1 // 标注坐标
            val (row, cell) = loc2 // 真实坐标
            val block = sheet.getRow(row).getCell(cell)
            val name = block?.toString() ?: continue
            var moveForward = true
            if (ignored.any { name.startsWith(it) }) {
                moveForward = false
            }
            var newY = if (moveForward) y + 1 else y // 如果是特殊情况则不往前挪
            if (newY > 6) newY = 1
            var targetLoc1 = seats[x to newY] ?: continue // 通过标注坐标获取目标坐标的真实坐标
            if (moveForward) {
                while (true) {
                    val (targetRow, targetCell) = targetLoc1
                    val targetName = sheet.getRow(targetRow).getCell(targetCell)?.toString() ?: continue
                    if (ignored.none { targetName.startsWith(it) }) {
                        break
                    }
                    newY += 1 // 如果是特殊情况则不往前挪
                    if (newY > 6) newY = 1
                    targetLoc1 = seats[x to newY] ?: continue // 通过标注坐标获取目标坐标的真实坐标
                }
            }
            prepareSeatsY += name to targetLoc1
        }
        for (name in prepareSeatsY.keys) {
            val (row, cell) = prepareSeatsY[name] ?: continue
            val block = sheet.getRow(row).getCell(cell)
            block.setCellValue(name)
        }
    }

    fun doArranging(file: File) {
        val input = FileInputStream(file)
        val book = XSSFWorkbook(input)
        val sheet = book.getSheetAt(0)
        doMoveSeats(sheet)
        doMoveSpecial(sheet)
        val output = FileOutputStream(file)
        book.write(output)
        output.close()
        book.close()
        input.close()
    }

    fun testSetup(file: File) {
        val input = FileInputStream(file)
        val book = XSSFWorkbook(input)
        val sheet = book.getSheetAt(0)
        for (loc1 in seats.keys) {
            val loc2 = seats[loc1] ?: continue
            val (x, y) = loc1
            val (row, cell) = loc2
            val block = sheet.getRow(row).getCell(cell)
            block.setCellValue("$block($x,$y)")
        }

        for (specialId in specialSeats.keys) {
            val (row, cell) = specialSeats[specialId] ?: continue
            val block = sheet.getRow(row).getCell(cell)
            block.setCellValue("$block($specialId)")
        }
        val output = FileOutputStream(file)
        book.write(output)
        output.close()
        book.close()
        input.close()
    }
}