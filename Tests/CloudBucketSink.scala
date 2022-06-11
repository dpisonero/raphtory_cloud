package com.raphtory.sinks

import com.raphtory.api.output.format.Format
import com.raphtory.api.output.sink.FormatAgnosticSink
import com.raphtory.api.output.sink.SinkConnector
import com.raphtory.api.output.sink.StreamSinkConnector
import com.raphtory.formats.CsvFormat
import com.typesafe.config.Config

import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.ReadChannel
import com.google.cloud.storage._
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.BlobId

/** A [[com.raphtory.api.output.sink.Sink Sink]] that writes a `Table` into files using the given `format`.
  *
  * The sink creates one directory with the job id as name inside `filepath`
  * and one file for every partition on the server inside that directory.
  * Then, it uploads the written file to a Google Cloud Bucket. It checks if the bucket exists, and creates
  * one if it isn't the case.
  * This Class has been created for Linux Systems, which is where it will run. On Windows, an adaptation of
  * the file paths would be needed
  *
  * @param filePath the filepath to write the table into
  * @param projectId the ID of the existing Google Cloud Project
  * @param bucketName the name of the Google Cloud Bucket. It creates one if it doesn't exist. This name has to be
  *                   unique in all of Google Cloud!
  * @param format the format to be used by this sink (`CsvFormat` by default)
  *
  * @example
  * {{{
  * import com.raphtory.algorithms.generic.EdgeList
  * import com.raphtory.sinks.FileSink
  * import com.raphtory.components.spout.instance.ResourceSpout
  *
  * val graphBuilder = new YourGraphBuilder()
  * val graph = Raphtory.stream(ResourceSpout("resource"), graphBuilder)
  * val testDir = "/tmp/raphtoryTest"
  * val sink = FileSink(testDir)
  *
  * graph.execute(EdgeList()).writeTo(sink)
  * }}}
  * @see [[com.raphtory.api.output.sink.Sink Sink]]
  *      [[com.raphtory.api.output.format.Format Format]]
  *      [[com.raphtory.formats.CsvFormat CsvFormat]]
  *      [[com.raphtory.api.analysis.table.Table Table]]
  *      [[com.raphtory.Raphtory Raphtory]]
  */
case class CloudBucketSink (filePath: String, projectId: String, bucketName: String, format: Format = CsvFormat())
  extends FormatAgnosticSink(format) {

  override def buildConnector(
     jobID: String,
     partitionID: Int,
     config: Config,
     itemDelimiter: String
   ): SinkConnector =
    new StreamSinkConnector(itemDelimiter) {
      private val workDirectory = s"$filePath/$jobID"
      new File(workDirectory).mkdirs()
      private val fileWriter    = new FileWriter(s"$workDirectory/partition-$partitionID")

      override def output(value: String): Unit = fileWriter.write(value)
      override def close(): Unit               = fileWriter.close()

      import com.google.cloud.storage.Storage
      import com.google.cloud.storage.StorageOptions

      // Instantiates a client
      val storage: Storage = StorageOptions.getDefaultInstance.getService

      // Creates the new bucket
      try {
        val bucket = storage.create(BucketInfo.of(bucketName))
        System.out.printf("Bucket %s created.%n", bucket.getName)
      } catch {
        case e: StorageException =>
          System.out.printf("Bucket already created. The name is %s.%n", bucketName)
      }
      val fileName = s"$workDirectory/partition-$partitionID"
      val blobId: BlobId = BlobId.of(bucketName, jobID + s"_partition-$partitionID")
      val blobInfo: BlobInfo = BlobInfo.newBuilder(blobId).build
      storage.create(blobInfo, Files.readAllBytes(Paths.get(fileName)))
    }

}
