package com.raphtory.output

import com.raphtory.algorithms.api.OutputFormat
import com.raphtory.algorithms.api.Row
import com.raphtory.time.Interval

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import com.google.cloud.storage.StorageException
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions

class CloudBucketOutputFormat(filePath: String, projectId: String, bucketName: String) extends OutputFormat {

  override def write(
    timestamp: Long,
    window: Option[Interval],
    jobID: String,
    row: Row,
    partitionID: Int
  ): Unit = {
    val dir = new File(s"$filePath/$jobID")

    if (!dir.exists())
    // TODO: Re-enable. Currently throws a NullPointerException
    //logger.debug(s"Output directory '$dir' does not exist. Creating directory...")
      dir.mkdirs()
    else {
      // TODO: Re-enable. Currently throws a NullPointerException
      //logger.warn(s"Output directory '$dir' already exists. Is the Job ID unique?")
    }

    val value = window match {
      case Some(w) => s"$timestamp,$w,${row.getValues().mkString(",")}\n"
      case None    => s"$timestamp,${row.getValues().mkString(",")}\n"
    }

    reflect.io.File(s"$filePath/$jobID/partition-$partitionID").appendAll(value)

    // TODO: Re-enable. Currently throws a NullPointerException
    //logger.info(s"Results successfully written out to directory '$dir'.")

    // Instantiates a client
    val storage: Storage = StorageOptions.getDefaultInstance.getService

    // Creates the new bucket
    try {
      val bucket = storage.create(BucketInfo.of(bucketName))
      System.out.printf("Bucket %s created.%n", bucket.getName)
    } catch {
      case e: StorageException =>
    }
    val fileName = s"$filePath/$jobID/partition-$partitionID"
    val blobId: BlobId = BlobId.of(bucketName, jobID + s"_partition-$partitionID")
    val blobInfo: BlobInfo = BlobInfo.newBuilder(blobId).build
    storage.create(blobInfo, Files.readAllBytes(Paths.get(fileName)))
  }
}

/** Writes output for Raphtory Job and Partition for a pre-defined window and timestamp to File */
object CloudBucketOutputFormat {

  /** @param filePath Filepath for writing Raphtory output. */
  def apply(filePath: String, projectId: String, bucketName: String) = new CloudBucketOutputFormat(filePath, projectId, bucketName)
}
