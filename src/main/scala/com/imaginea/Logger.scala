package com.imaginea

import org.slf4j.LoggerFactory

/**
 *  it can be mixedin a class which needs logging
 */

trait Logger {
  val logger = LoggerFactory.getLogger(this.getClass)
}
