package com.imaginea

import org.slf4j.LoggerFactory

trait Logger {
  val logger = LoggerFactory.getLogger(this.getClass)
}