package com.raphtory.examples.lotrTopic

import com.raphtory.Raphtory
import com.raphtory.api.analysis.graphview.DeployedTemporalGraph
import com.raphtory.api.querytracker.QueryProgressTracker
import com.raphtory.examples.lotrTopic.analysis.DegreesSeparation
import com.raphtory.examples.lotrTopic.graphbuilders.LOTRGraphBuilder
import com.raphtory.sinks.CloudBucketSink
import com.raphtory.spouts.FileSpout
import com.raphtory.utils.FileUtils

object CloudBucketRunner extends App{

  val path = "/tmp/lotr.csv"
  val url  = "https://raw.githubusercontent.com/Raphtory/Data/main/lotr.csv"

  FileUtils.curlFile(path, url)

  val source: FileSpout[String] = FileSpout(path)
  val builder = new LOTRGraphBuilder()
  val graph: DeployedTemporalGraph = Raphtory.load(spout = source, graphBuilder = builder)
  val output: CloudBucketSink = CloudBucketSink("/tmp/raphtory",
    "ardent-quarter-347510", "first-bucket-test-raphtory-mine")

  val queryHandler: QueryProgressTracker = graph
    .at(32674)
    .past()
    .execute(DegreesSeparation())
    .writeTo(output)

  queryHandler.waitForJob()

}
