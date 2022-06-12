package com.raphtory.examples.lotrTopic

import com.raphtory.algorithms.generic.ConnectedComponents
import com.raphtory.deployment.Raphtory
import com.raphtory.examples.lotrTopic.CloudBucketOutputRunner.{graph, output}
import com.raphtory.examples.lotrTopic.analysis.DegreesSeparation
import com.raphtory.output.{CloudBucketOutputFormat, FileOutputFormat}

object LOTRClient extends App {

  val client = Raphtory.connect()

  val output  = CloudBucketOutputFormat("/tmp/raphtory", "ardent-quarter-347510", "first-bucket-test-raphtory-mine")

  client.execute(ConnectedComponents()).writeTo(output)
  
}
