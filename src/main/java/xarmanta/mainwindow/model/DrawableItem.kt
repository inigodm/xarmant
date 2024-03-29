package xarmanta.mainwindow.model

import javafx.scene.paint.Color
import xarmanta.mainwindow.model.commit.Type

data class DrawableItem(val type: Type,
                        val fromX: Double,
                        val toX: Double,
                        val fromY: Double,
                        val toY: Double,
                        val color: Color = Color.RED,
                        val size: Double = 1.0)
