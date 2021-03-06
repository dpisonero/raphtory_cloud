package com.raphtory.examples.lotrTopic

import com.raphtory.components.spout.Spout
import com.raphtory.deployment.RaphtoryService
import com.raphtory.examples.lotrTopic.graphbuilders.LOTRGraphBuilder
import com.raphtory.spouts.{CloudSpout, FileSpout}
import com.raphtory.util.FileUtils
import com.typesafe.config.ConfigFactory

object LOTRService extends RaphtoryService[String] {

  val path = "/tmp/lotr.csv"
  val url  = "https://raw.githubusercontent.com/Raphtory/Data/main/lotr.csv"

  //FileUtils.curlFile(path, url)


  override def defineSpout(): Spout[String] = CloudSpout("ardent-quarter-347510", "first-bucket-test-raphtory-mine", "lotr.csv")

  override def defineBuilder: LOTRGraphBuilder = new LOTRGraphBuilder()

}
