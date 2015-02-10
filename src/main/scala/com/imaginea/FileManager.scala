package com.imaginea

import java.io.PrintWriter
import java.io.File
import java.io.FileWriter

/**
 * utility for creating folders
 */

trait FolderManager {
  def createFolder(path: String) { new File(path).mkdirs }
}

/**
 * utility for writing data into file
 */

trait FileContentAppender {

  def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try { f(param) } finally { param.close() }

  /**
   * this method append the data into files
   */

  def appendToFile(fileName: String, textData: String): PrintWriter =
    using(new FileWriter(fileName, true)) {
      fileWriter =>
        using(new PrintWriter(fileWriter)) {
          printWriter => printWriter.append(textData)
        }
    }

}
