package com.raphtory.examples.lotrTopic

import com.raphtory.deployment.Raphtory
import com.raphtory.examples.lotrTopic.analysis.DegreesSeparation
import com.raphtory.examples.lotrTopic.graphbuilders.LOTRGraphBuilder
import com.raphtory.output.CloudBucketOutputFormat
import com.raphtory.spouts.{CloudSpout, FileSpout}
import com.raphtory.util.FileUtils

object CloudBucketOutputRunner extends App {
  val path = "/tmp/lotr.csv"
  val url = "https://raw.githubusercontent.com/Raphtory/Data/main/lotr.csv"

  FileUtils.curlFile(path, url)

  //val source = FileSpout(path)
  val source  = CloudSpout("ardent-quarter-347510", "first-bucket-test-raphtory-mine", "lotr.csv")
  val builder = new LOTRGraphBuilder()
  val graph = Raphtory.load(spout = source, graphBuilder = builder)
  val output = CloudBucketOutputFormat("/tmp/raphtory", "ardent-quarter-347510", "first-bucket-test-raphtory-mine")

  val queryHandler = graph
    .at(32674)
    .past()
    .execute(DegreesSeparation())
    .writeTo(output)

  queryHandler.waitForJob()

}
