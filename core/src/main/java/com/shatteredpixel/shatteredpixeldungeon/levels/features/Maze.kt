/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels.features

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Random
import com.watabou.utils.Rect

object Maze {

    var EMPTY = false
    var FILLED = true

    fun generate(r: Room): Array<BooleanArray> {
        val maze = Array(r.width()) { BooleanArray(r.height()) }

        for (x in maze.indices) {
            for (y in 0 until maze[0].size) {
                if (x == 0 || x == maze.size - 1 ||
                        y == 0 || y == maze[0].size - 1) {

                    maze[x][y] = FILLED

                }
            }
        }

        //set spaces where there are doors
        for (d in r.connected.values) {
            maze[d.x - r.left][d.y - r.top] = EMPTY
        }

        return generate(maze)
    }

    fun generate(r: Rect): Array<BooleanArray> {
        return generate(r.width() + 1, r.height() + 1)
    }

    fun generate(width: Int, height: Int): Array<BooleanArray> {
        return generate(Array(width) { BooleanArray(height) })
    }

    fun generate(maze: Array<BooleanArray>): Array<BooleanArray> {
        var fails = 0
        var x: Int
        var y: Int
        var moves: Int
        var mov: IntArray?
        while (fails < 2500) {

            //find a random wall point
            do {
                x = Random.Int(maze.size)
                y = Random.Int(maze[0].size)
            } while (!maze[x][y])

            //decide on how we're going to move
            mov = decideDirection(maze, x, y)
            if (mov == null) {
                fails++
            } else {

                fails = 0
                moves = 0
                do {
                    x += mov[0]
                    y += mov[1]
                    maze[x][y] = FILLED
                    moves++
                } while (Random.Int(moves) == 0 && checkValidMove(maze, x, y, mov))

            }

        }

        return maze
    }

    private fun decideDirection(maze: Array<BooleanArray>, x: Int, y: Int): IntArray? {

        //attempts to move up
        if (Random.Int(4) == 0 && //1 in 4 chance
                checkValidMove(maze, x, y, intArrayOf(0, -1))) {
            return intArrayOf(0, -1)
        }

        //attempts to move right
        if (Random.Int(3) == 0 && //1 in 3 chance
                checkValidMove(maze, x, y, intArrayOf(1, 0))) {
            return intArrayOf(1, 0)
        }

        //attempts to move down
        if (Random.Int(2) == 0 && //1 in 2 chance
                checkValidMove(maze, x, y, intArrayOf(0, 1))) {
            return intArrayOf(0, 1)
        }

        //attempts to move left
        return if (checkValidMove(maze, x, y, intArrayOf(-1, 0))) {
            intArrayOf(-1, 0)
        } else null

    }

    private fun checkValidMove(maze: Array<BooleanArray>, x: Int, y: Int, mov: IntArray): Boolean {
        var x = x
        var y = y
        val sideX = 1 - Math.abs(mov[0])
        val sideY = 1 - Math.abs(mov[1])

        //checking two tiles forward in the movement, and the tiles to their left/right
        for (i in 0..1) {
            x += mov[0]
            y += mov[1]
            //checks if tiles we're examining are valid and open
            return if (x > 0 && x < maze.size - 1 && y > 0 && y < maze[0].size - 1 &&
                    !maze[x][y] && !maze[x + sideX][y + sideY] && !maze[x - sideX][y - sideY]) {
                continue
            } else {
                false
            }
        }
        return true
    }
}
