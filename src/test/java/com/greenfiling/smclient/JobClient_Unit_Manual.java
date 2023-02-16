/**
 * Copyright 2021-2023 Green Filing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greenfiling.smclient;

import static com.greenfiling.smclient.TestHelper.log;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greenfiling.smclient.internal.DnsSelector;
import com.greenfiling.smclient.internal.DnsSelector.IpMode;
import com.greenfiling.smclient.model.Attachment;
import com.greenfiling.smclient.model.Attempt;
import com.greenfiling.smclient.model.AttemptSubmit;
import com.greenfiling.smclient.model.Company;
import com.greenfiling.smclient.model.Job;
import com.greenfiling.smclient.model.Links;
import com.greenfiling.smclient.model.Recipient;
import com.greenfiling.smclient.model.ServiceDocument;
import com.greenfiling.smclient.model.exchange.FilterDateRange;
import com.greenfiling.smclient.model.exchange.Index;
import com.greenfiling.smclient.model.exchange.JobFilter;
import com.greenfiling.smclient.model.exchange.Show;
import com.greenfiling.smclient.model.internal.FilterBase;
import com.greenfiling.smclient.model.internal.JobBase;

import de.westemeyer.version.model.Artifact;
import de.westemeyer.version.service.ArtifactVersionCollector;

public class JobClient_Unit_Manual {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(JobClient_Unit_Manual.class);

  @BeforeClass
  public static void setUpClass() {
    TestHelper.loadTestResources();
    // System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
  }

  @Test
  public void testConstructor_HappyPath() throws Exception {
    boolean caughtException = false;
    JobClient client = null;
    ApiHandle apiHandle = null;
    try {
      apiHandle = TestHelper.getApiHandle();
    } catch (IllegalStateException e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(false));
    assertThat(apiHandle, not(equalTo(null)));

    caughtException = false;
    try {
      client = new JobClient(apiHandle);
    } catch (Exception e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(false));
    assertThat(client, not(equalTo(null)));
  }

  @Test
  public void testCreateJob_HappyPath() throws Exception {

    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Job newJob = TestHelper.getTestJob();

    Show<Job> response = client.create(newJob);
    Links links = response.getData().getLinks();
    log("job created, links.self = %s", links.getSelf());

    // JobClient jobClient = new JobClient(apiHandle);
    // JobSubmit job = TestHelper.getTestJobSubmit("job 2");
    // job.setCourtCaseId(1234);
    // job.setRush(true);
    // job.setDueDate(LocalDate.parse("2021-11-15"));
    // Show<Job> createdJob = jobClient.create(job);
    // log("New job " + createdJob.getData().getId() + " visible at " + createdJob.getData().getLinks().getSelf());

  }

  @Test
  public void testCreateJob_JobTypeId() throws Exception {

    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Job newJob = TestHelper.getTestJob();
    newJob.setJobTypeId(JobBase.JOB_TYPE_SOP);

    Show<Job> response = client.create(newJob);
    Links links = response.getData().getLinks();
    log("job created, links.self = %s", links.getSelf());

    // JobClient jobClient = new JobClient(apiHandle);
    // JobSubmit job = TestHelper.getTestJobSubmit("job 2");
    // job.setCourtCaseId(1234);
    // job.setRush(true);
    // job.setDueDate(LocalDate.parse("2021-11-15"));
    // Show<Job> createdJob = jobClient.create(job);
    // log("New job " + createdJob.getData().getId() + " visible at " + createdJob.getData().getLinks().getSelf());

  }

  // @Test
  // public void test_readJob() throws Exception {
  //
  // ApiHandle apiHandle = new ApiHandle.Builder().apiKey(VALID_API_KEY).apiEndpoint(ApiHandle.DEFAULT_ENDPOINT_BASE).build();
  // JobClient client = new JobClient(apiHandle);
  //
  // Show<Job> resp = client.show(11051692);
  //
  // log("Job Number: %s", resp.getData().getServeManagerJobNumber());
  // log("Job Type ID: %s", resp.getData().getJobTypeId());
  // }

  @Test
  public void testCreateJob_CustomData() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Job newJob = TestHelper.getTestJob();

    // Doesn't seem to actually do anything. Still haven't heard what good this is
    HashMap<String, String> custom = new HashMap<String, String>();
    custom.put("test_custom_key", "test_custom_value");
    newJob.setCustom(custom);

    Show<Job> response = client.create(newJob);
    Links links = response.getData().getLinks();
    log("job created, links.self = %s", links.getSelf());
  }

  @Test
  public void testDate() throws Exception {
    // "updated_at":"2021-10-26T15:32:34-04:00"
    String dateString = "2021-10-26T15:32:34-06:00";
    String format = "yyyy-MM-dd'T'HH:mm:ssXXX";
    // format = "yyyy-MM-dd'T'HH:mm:ss";
    DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
    Date date = dateFormat.parse(dateString);
    log("date = %s", date.toString());

    DateFormat dateFormatCur = new SimpleDateFormat(format, Locale.US);
    Date curDate = new Date();
    log(dateFormatCur.format(curDate));
  }

  @Test
  public void testFoo() throws Exception {
    Attempt attempt = new Attempt();
    attempt.setServeType(Attempt.SERVE_TYPE_SUCCESS_AUTHORIZED);

    AttemptSubmit attemptSubmit = new AttemptSubmit();
    attemptSubmit.setRecipientEthnicity(Recipient.ETHNICITY_CAUCASIAN);

    Job job = TestHelper.getTestJob();
    job.setJobStatus(Job.JOB_STATUS_CANCELED);

    Company company = new Company();
    company.setCompanyType(Company.COMPANY_TYPE_CONTRACTOR);

  }

  @Test
  public void testGetFile() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    String url = "http://speedtest.ftp.otenet.gr/files/test100k.db";
    String localPath = System.getProperty("java.io.tmpdir") + "/test-file.out";

    client.getFile(url, localPath);
    log("Downloaded to %s", localPath);
  }

  @Test
  public void testGetFile2() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Show<Job> resp = client.show(8624870);
    ServiceDocument doc = resp.getData().getDocumentsToBeServed().get(0);

    String localPath = System.getProperty("java.io.tmpdir") + "/" + doc.getUpload().getFileName();

    client.getFile(doc.getUpload().getLinks().getDownloadUrl(), localPath);
    log("Downloaded from %s", doc.getUpload().getLinks().getDownloadUrl());
    log("Downloaded to %s", localPath);
  }

  @Test
  public void testGetNext_HappyPath() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Index<Job> response = client.index();
    while (response != null) {
      Links links = response.getLinks();
      log("links.self = %s", links.getSelf());

      ArrayList<Job> jobs = response.getData();
      log("Number of jobs in response: %s", jobs.size());

      response = client.getNext(response);
    }

    // JobClient jobClient = new JobClient(apiHandle);
    //
    // Index<Job> resp = jobClient.index();
    // Integer pages = 0;
    // Integer total = 0;
    // while (resp != null) {
    // pages++;
    // total += resp.getData().size();
    // response = client.getNext(response);
    // }
    // log(total + " total objects returned across " + pages + " pages");

  }

  @Test
  public void testIndexJob_Filter_DateRange() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    JobFilter filter = new JobFilter();
    filter.setDateRange(new FilterDateRange());
    filter.getDateRange().setType(FilterDateRange.TYPE_CREATED_AT);
    filter.getDateRange().setMax(OffsetDateTime.parse("2021-11-01T00:00:00-00:00"));

    Index<Job> response = client.index(filter);
    Links links = response.getLinks();
    log("links.self = %s", links.getSelf());

    ArrayList<Job> jobs = response.getData();
    log("Number of jobs in response: %s", jobs.size());

    // // Create a job filter that lists all jobs that have the test foobar in them and were created after October 1, 2021
    // JobFilter jobFilter = new JobFilter();
    // jobFilter.setDateRange(new FilterDateRange());
    // jobFilter.getDateRange().setType(FilterDateRange.TYPE_CREATED_AT);
    // jobFilter.getDateRange().setMin(OffsetDateTime.parse("2021-10-01T00:00:00-00:00"));
    // jobFilter.setQ("foobar");
    //
    // JobClient jobClient = new JobClient(apiHandle);
    // Index<Job> resp = jobClient.index(filter);
    //
    // log("Matching jobs:");
    // while (resp != null) {
    // for (Job j : resp.getData()) {
    // log(" - JobId: " + j.getId() + ", URL = " + j.getLinks().getSelf());
    // }
    // resp = jobClient.getNext(resp);
    // }

  }

  @Test
  public void testIndexJob_ExternalBuilder() throws Exception {
    okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder().dns(new DnsSelector(IpMode.IPV4_ONLY));
    ApiHandle apiHandle = new ApiHandle.Builder().builder(builder).apiKey(TestHelper.VALID_API_KEY).build();
    JobClient client = new JobClient(apiHandle);

    Index<Job> response = client.index();
    Links links = response.getLinks();
    log("links.self = %s", links.getSelf());

    ArrayList<Job> jobs = response.getData();
    log("Number of jobs in response: %s", jobs.size());
  }

  @Test
  public void testIndexJob_Filter_HappyPath() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    JobFilter filter = new JobFilter();
    filter.setArchiveState(FilterBase.ARCHIVE_STATE_ARCHIVED);
    // filter.setQ("random text");
    filter.getJobStatus().add(JobFilter.JOB_STATUS_CANCELED);
    filter.getJobStatus().add(JobFilter.JOB_STATUS_FILED);

    Index<Job> response = client.index(filter);

    Links links = response.getLinks();
    log("links.self = %s", links.getSelf());

    ArrayList<Job> jobs = response.getData();
    log("Number of jobs in response: %s", jobs.size());
  }

  @Test
  public void testIndexJob_HappyPath() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Index<Job> response = client.index();
    Links links = response.getLinks();
    log("links.self = %s", links.getSelf());

    ArrayList<Job> jobs = response.getData();
    log("Number of jobs in response: %s", jobs.size());

    // JobClient jobClient = new JobClient(apiHandle);
    // Index<Job> resp = jobClient.index();
    // log("Number of jobs in response: %s", resp.getData().size());
    // log("ServeManager Job Numbers: ");
    // for (Job j : resp.getData()) {
    // log(" - %s", j.getServeManagerJobNumber());
    // }

  }

  @Test
  public void testLoadConfig() throws Exception {
    log("VALID_API_KEY: %s", TestHelper.VALID_API_KEY);
    log("VALID_FILE_1: %s", TestHelper.VALID_FILE_PATH_1);
    log("VALID_FILE_2: %s", TestHelper.VALID_FILE_PATH_2);
  }

  // because setEndPoint() is protected, we can't run this anymore. To test, do a custom extension of ApiClient that has the wrong endpoint
  // @Test
  // public void testShowJob_BadApiEndpoint() throws Exception {
  // boolean caughtException = false;
  // ApiHandle apiHandle = new ApiHandle.Builder().apiKey(VALID_API_KEY).apiEndpoint(ApiHandle.DEFAULT_ENDPOINT_BASE).build();
  // JobClient client = new JobClient(apiHandle);
  // client.setEndpoint("foo");
  // Show<Job> showResp = null;
  // try {
  // showResp = client.show(8559826);
  // } catch (Exceptions.InvalidEndpointException e) {
  // caughtException = true;
  // }
  // assertThat(caughtException, equalTo(true));
  // assertThat(showResp, equalTo(null));
  // }

  @Test
  public void testShowJob_BadAuth() throws Exception {
    boolean caughtException = false;
    ApiHandle apiHandle = new ApiHandle.Builder().apiKey("foo").apiEndpoint(ApiHandle.DEFAULT_ENDPOINT_BASE).build();
    JobClient client = new JobClient(apiHandle);
    Show<Job> showResp = null;
    try {
      showResp = client.show(8559826);
    } catch (Exceptions.InvalidCredentialsException e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(true));
    assertThat(showResp, equalTo(null));
  }

  @Test
  public void testShowJob_BadEndpointBase() throws Exception {
    boolean caughtException = false;
    ApiHandle apiHandle = new ApiHandle.Builder().apiKey(TestHelper.VALID_API_KEY).apiEndpoint("https://jetmore.org/api").build();
    JobClient client = new JobClient(apiHandle);
    Show<Job> showResp = null;
    try {
      showResp = client.show(8559826);
    } catch (Exceptions.InvalidEndpointException e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(true));
    assertThat(showResp, equalTo(null));
  }

  @Test
  public void testShowJob_BadEndpointServer() throws Exception {
    boolean caughtException = false;
    ApiHandle apiHandle = new ApiHandle.Builder().connectTimeout(2).apiKey(TestHelper.VALID_API_KEY)
        .apiEndpoint("https://adfadsf234iukjawdfkhwe3333333.org/api").build();
    JobClient client = new JobClient(apiHandle);
    Show<Job> showResp = null;
    try {
      showResp = client.show(8559826);
    } catch (UnknownHostException | ConnectException e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(true));
    assertThat(showResp, equalTo(null));
  }

  @Test
  public void testShowJob_HappyPath() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Show<Job> response = client.show(8559826);
    Links links = response.getData().getLinks();
    log("links.self = %s", links.getSelf());
    log("type = %s", response.getData().getType());
    log("updated_at = %s", response.getData().getUpdatedAt());
    log("recipient.name = %s", response.getData().getRecipient().getName());
    log("dueDate = %s", response.getData().getDueDate());
    // log(Util.printObject(response.getData()));

    OffsetDateTime updatedAt = response.getData().getUpdatedAt().minusDays(3);
    log("3 days before updatedAt = %s", updatedAt);

    // JobClient jobClient = new JobClient(apiHandle);
    // Show<Job> resp = jobClient.show(123);
    // log("Job Number: %s", resp.getData().getServeManagerJobNumber());
    // log("Documents for download: ");
    // for (Document d : resp.getData().getDocuments()) {
    // log(" - %s", d.getPdfDownloadUrl());
    // }

  }

  @Test
  public void testShowJob_NoSuchObject() throws Exception {
    boolean caughtException = false;
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);
    Show<Job> showResp = null;
    try {
      showResp = client.show(1);
    } catch (Exceptions.RecordNotFoundException e) {
      caughtException = true;
    }
    assertThat(caughtException, equalTo(true));
    assertThat(showResp, equalTo(null));
  }

  @Test
  public void testUpdateJob_HappyPath() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Job newJob = TestHelper.getTestJob();
    Integer jobId = 8595080;
    newJob.setJobStatus(Job.JOB_STATUS_ON_HOLD);
    newJob.setRush(true);
    newJob.setDueDate(LocalDate.parse("2021-11-07"));

    Show<Job> response = client.update(jobId, newJob);
    Links links = response.getData().getLinks();
    log("job created, links.self = %s", links.getSelf());

    // JobSubmit job = TestHelper.getTestJobSubmit("job 2");
    // job.setRush(true);
    //
    // JobClient jobClient = new JobClient(apiHandle);
    // Show<Job> createdJob = jobClient.update(1234, job);
    // log("Job " + createdJob.getData().getId() + " updated, new rush = " + createdJob.getData().getRush());

  }

  @Test
  public void testArtifactVersionService() throws Exception {
    log("List of artifacts:");
    for (Artifact artifact : ArtifactVersionCollector.collectArtifacts()) {
      log("artifact = %s", artifact);
    }
    Artifact smclient = ArtifactVersionCollector.findArtifact("com.greenfiling.smclient", "servemanager-client");
    log("name = %s, version = %s", smclient.getName(), smclient.getVersion());
  }

  @Test
  public void testUpdateJob_CheckForDelayOnAttachmentUpload() throws Exception {
    ApiHandle apiHandle = TestHelper.getApiHandle();
    JobClient client = new JobClient(apiHandle);

    Job newJob = TestHelper.getTestJob();

    Show<Job> response = client.create(newJob);
    Links links = response.getData().getLinks();
    llog(String.format("job created, links.self = %s", links.getSelf()));

    ArrayList<Attachment> docs = new ArrayList<>();
    Attachment doc = new Attachment();
    doc.setFileName("file_name.pdf");
    docs.add(doc);

    Job job = response.getData();
    job.setMiscAttachments(docs);
    response = client.update(job.getId(), job);
    llog("attachment added");
    for (Attachment a : response.getData().getMiscAttachments()) {
      client.completeUpload(a.getUpload(), "application/pdf", new File(TestHelper.VALID_FILE_PATH_1));
      llog("attachment uploaded");
    }

    // for (int i = 0; i < 100; i++) {
    response = client.show(job.getId());
    llog(String.format("polled for misc_attachments, _count = %d, real count = %d", response.getData().getMiscAttachmentsCount(),
        response.getData().getMiscAttachments().size()));
    // }

    // JobSubmit job = TestHelper.getTestJobSubmit("job 2");
    // job.setCourtCaseId(1234);
    // job.setRush(true);
    // job.setDueDate(LocalDate.parse("2021-11-15"));
    // Show<Job> createdJob = client.create(job);
    // log("New job " + createdJob.getData().getId() + " visible at " + createdJob.getData().getLinks().getSelf());

  }

  private void llog(String msg) {
    log("%s: %s", LocalTime.now(), msg);
  }
}
