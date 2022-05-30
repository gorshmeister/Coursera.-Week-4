package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(createSquareBoard(width))


open class SquareBoardImpl(override val width: Int) : SquareBoard {
    private val listOfList = arrayListOf<List<Cell>>()

    init {
        for (row in 1..width) {
            val list = arrayListOf<Cell>()
            for (column in 1..width) {
                list.add(Cell(row, column))
            }
            listOfList.add(list)
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return listOfList
            .flatMap { list -> list }
            .find { cell -> cell == Cell(i, j) }
    }

    override fun getCell(i: Int, j: Int): Cell {
        return getCellOrNull(i, j)
            ?: throw IllegalArgumentException()
    }

    override fun getAllCells(): Collection<Cell> {
        return listOfList.flatten()
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val result = mutableListOf<Cell>()
        jRange.forEach { cell ->
            try {
                result += listOfList[i - 1][cell - 1]
            } catch (e: IndexOutOfBoundsException) {
                return result
            }
        }
        return result

    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val result = mutableListOf<Cell>()
        iRange.forEach { cell ->
            try {
                result += listOfList[cell - 1][j - 1]
            } catch (e: IndexOutOfBoundsException) {
                return result
            }
        }
        return result
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            UP -> getCellOrNull(i - 1, j)
            DOWN -> getCellOrNull(i + 1, j)
            LEFT -> getCellOrNull(i, j - 1)
            RIGHT -> getCellOrNull(i, j + 1)
        }
    }
}


//operator fun <T> GameBoard<T>.get(i: Int, j: Int) = get(getCell(i, j))
//operator fun <T> GameBoard<T>.set(i: Int, j: Int, value: T) = set(getCell(i, j), value)


class GameBoardImpl<T>(private val squareBoard: SquareBoard) : SquareBoard by squareBoard, GameBoard<T> {
    val map = mutableMapOf<Cell, T?>()

    init {
        squareBoard
            .getAllCells()
            .forEach { cell ->
                map[cell] = null
            }
    }

    override fun get(cell: Cell): T? {
        return map[cell]
    }

    override fun set(cell: Cell, value: T?) {
        map.set(cell, value)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return map.all { predicate(it.value) }
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return map.any { predicate(it.value) }
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return filter(predicate).firstOrNull()
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return map.filter { predicate(it.value) }.keys
    }
}





