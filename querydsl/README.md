## After generating Q type with QueryDsl for example the QBatchJobExecution:

```java
    @Generated("com.querydsl.sql.codegen.MetaDataSerializer")
    public class QBatchJobExecution extends com.querydsl.sql.RelationalPathBase<QBatchJobExecution> {

      private static final long serialVersionUID = -839856780;

      public static final QBatchJobExecution batchJobExecution = new QBatchJobExecution("BATCH_JOB_EXECUTION");

      public final DateTimePath<java.sql.Timestamp> createTime = createDateTime("createTime", java.sql.Timestamp.class);

      public final DateTimePath<java.sql.Timestamp> endTime = createDateTime("endTime", java.sql.Timestamp.class);

      public final StringPath exitCode = createString("exitCode");

      public final StringPath exitMessage = createString("exitMessage");

      public final StringPath jobConfigurationLocation = createString("jobConfigurationLocation");

      public final NumberPath<Long> jobExecutionId = createNumber("jobExecutionId", Long.class);

      public final NumberPath<Long> jobInstanceId = createNumber("jobInstanceId", Long.class);

      public final DateTimePath<java.sql.Timestamp> lastUpdated = createDateTime("lastUpdated", java.sql.Timestamp.class);

      public final DateTimePath<java.sql.Timestamp> startTime = createDateTime("startTime", java.sql.Timestamp.class);

      public final StringPath status = createString("status");

      public final NumberPath<Long> version = createNumber("version", Long.class);

      public final com.querydsl.sql.PrimaryKey<QBatchJobExecution> primary = createPrimaryKey(jobExecutionId);

      public final com.querydsl.sql.ForeignKey<QBatchJobInstance> jobInstExecFk = createForeignKey(Arrays.asList(jobInstanceId, jobInstanceId), Arrays.asList("JOB_INSTANCE_ID", "JOB_INSTANCE_ID"));

      public final com.querydsl.sql.ForeignKey<QBatchJobExecutionContext> _jobExecCtxFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

      public final com.querydsl.sql.ForeignKey<QBatchJobExecutionParams> _jobExecParamsFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

      public final com.querydsl.sql.ForeignKey<QBatchStepExecution> _jobExecStepFk = createInvForeignKey(jobExecutionId, "JOB_EXECUTION_ID");

      public QBatchJobExecution(String variable) {
          super(QBatchJobExecution.class, PathMetadataFactory.forVariable(variable), "null", "BATCH_JOB_EXECUTION");
          addMetadata();
      }

      public QBatchJobExecution(String variable, String schema, String table) {
          super(QBatchJobExecution.class, PathMetadataFactory.forVariable(variable), schema, table);
          addMetadata();
      }

      public QBatchJobExecution(String variable, String schema) {
          super(QBatchJobExecution.class, PathMetadataFactory.forVariable(variable), schema, "BATCH_JOB_EXECUTION");
          addMetadata();
      }

      public QBatchJobExecution(Path<? extends QBatchJobExecution> path) {
          super(path.getType(), path.getMetadata(), "null", "BATCH_JOB_EXECUTION");
          addMetadata();
      }

      public QBatchJobExecution(PathMetadata metadata) {
          super(QBatchJobExecution.class, metadata, "null", "BATCH_JOB_EXECUTION");
          addMetadata();
      }

      public void addMetadata() {
          addMetadata(createTime, ColumnMetadata.named("CREATE_TIME").withIndex(4).ofType(Types.TIMESTAMP).withSize(26).notNull());
          addMetadata(endTime, ColumnMetadata.named("END_TIME").withIndex(6).ofType(Types.TIMESTAMP).withSize(26));
          addMetadata(exitCode, ColumnMetadata.named("EXIT_CODE").withIndex(8).ofType(Types.VARCHAR).withSize(2500));
          addMetadata(exitMessage, ColumnMetadata.named("EXIT_MESSAGE").withIndex(9).ofType(Types.VARCHAR).withSize(2500));
          addMetadata(jobConfigurationLocation, ColumnMetadata.named("JOB_CONFIGURATION_LOCATION").withIndex(11).ofType(Types.VARCHAR).withSize(2500));
          addMetadata(jobExecutionId, ColumnMetadata.named("JOB_EXECUTION_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
          addMetadata(jobInstanceId, ColumnMetadata.named("JOB_INSTANCE_ID").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
          addMetadata(lastUpdated, ColumnMetadata.named("LAST_UPDATED").withIndex(10).ofType(Types.TIMESTAMP).withSize(26));
          addMetadata(startTime, ColumnMetadata.named("START_TIME").withIndex(5).ofType(Types.TIMESTAMP).withSize(26));
          addMetadata(status, ColumnMetadata.named("STATUS").withIndex(7).ofType(Types.VARCHAR).withSize(10));
          addMetadata(version, ColumnMetadata.named("VERSION").withIndex(2).ofType(Types.BIGINT).withSize(19));
      }
  }
```

## Create a BatchJobExecution that extends QueryDslEntity<QBatchJobExecution>, implements newInstance and the init method thats will be used by repository class for querying. init method allows to define via lambda getter method, setter method and join with other table. There are two type of join oneToOne and oneToMany

```java
    public class BatchJobExecution extends QueryDslEntity<QBatchJobExecution> {

      private static final QBatchJobExecution batchJobExecution = QBatchJobExecution.batchJobExecution;

      private long jobExecutionId;
      private long version;
      private long jobInstanceId;
      private Timestamp createTime;
      private Timestamp startTime;
      private Timestamp endTime;
      private String status;
      private String exitCode;
      private String exitMessage;
      private Timestamp lastUpdated;
      private String jobConfigurationLocation;
      private List<BatchJobExecutionParams> jobExecutionParams = new ArrayList<>();
      private List<BatchStepExecution> stepExecutions = new ArrayList<>();
      private List<BatchJobExecutionContext> jobExecutionContexts = new ArrayList<>();

      public BatchJobExecution() {
          super(batchJobExecution);
      }

      @Override
      public BatchJobExecution newInstance() {
          return new BatchJobExecution();
      }

      @Override
      protected void init() {
          field(batchJobExecution.jobExecutionId, this::getJobExecutionId, (t) -> setJobExecutionId(t.get(batchJobExecution.jobExecutionId)), true);
          field(batchJobExecution.version, this::getVersion, (t) -> setVersion(t.get(batchJobExecution.version)));
          field(batchJobExecution.jobInstanceId, this::getJobInstanceId, (t) -> setJobInstanceId(t.get(batchJobExecution.jobInstanceId)));
          field(batchJobExecution.createTime, this::getCreateTime, (t) -> setCreateTime(t.get(batchJobExecution.createTime)));
          field(batchJobExecution.startTime, this::getStartTime, (t) -> setStartTime(t.get(batchJobExecution.startTime)));
          field(batchJobExecution.endTime, this::getEndTime, (t) -> setEndTime(t.get(batchJobExecution.endTime)));
          field(batchJobExecution.status, this::getStatus, (t) -> setStatus(t.get(batchJobExecution.status)));
          field(batchJobExecution.exitCode, this::getExitCode, (t) -> setExitCode(t.get(batchJobExecution.exitCode)));
          field(batchJobExecution.exitMessage, this::getExitMessage, (t) -> setExitMessage(t.get(batchJobExecution.exitMessage)));
          field(batchJobExecution.lastUpdated, this::getLastUpdated, (t) -> setLastUpdated(t.get(batchJobExecution.lastUpdated)));
          field(batchJobExecution.jobConfigurationLocation, this::getJobConfigurationLocation, (t) -> setJobConfigurationLocation(t.get(batchJobExecution.jobConfigurationLocation)));

          BatchJobExecutionParams jobExecParamsFk = new BatchJobExecutionParams();
          joinOneToMany(jobExecParamsFk, jobExecParamsFk.QType().jobExecutionId.eq(batchJobExecution.jobExecutionId), o -> jobExecutionParams.add(o));

          BatchStepExecution jobExecStepFk = new BatchStepExecution();
          joinOneToMany(jobExecStepFk, jobExecStepFk.QType().jobExecutionId.eq(batchJobExecution.jobExecutionId), o -> stepExecutions.add(o));

          BatchJobExecutionContext jobExecCtxFk = new BatchJobExecutionContext();
          joinOneToMany(jobExecCtxFk, jobExecCtxFk.QType().jobExecutionId.eq(batchJobExecution.jobExecutionId), o -> jobExecutionContexts.add(o));
      }

      public long getJobExecutionId() {
          return jobExecutionId;
      }

      public void setJobExecutionId(long jobExecutionId) {
          this.jobExecutionId = jobExecutionId;
      }

      public long getVersion() {
          return version;
      }

      public void setVersion(long version) {
          this.version = version;
      }

      public long getJobInstanceId() {
          return jobInstanceId;
      }

      public void setJobInstanceId(long jobInstanceId) {
          this.jobInstanceId = jobInstanceId;
      }

      public Timestamp getCreateTime() {
          return createTime;
      }

      public void setCreateTime(Timestamp createTime) {
          this.createTime = createTime;
      }

      public Timestamp getStartTime() {
          return startTime;
      }

      public void setStartTime(Timestamp startTime) {
          this.startTime = startTime;
      }

      public Timestamp getEndTime() {
          return endTime;
      }

      public void setEndTime(Timestamp endTime) {
          this.endTime = endTime;
      }

      public String getStatus() {
          return status;
      }

      public void setStatus(String status) {
          this.status = status;
      }

      public String getExitCode() {
          return exitCode;
      }

      public void setExitCode(String exitCode) {
          this.exitCode = exitCode;
      }

      public String getExitMessage() {
          return exitMessage;
      }

      public void setExitMessage(String exitMessage) {
          this.exitMessage = exitMessage;
      }

      public Timestamp getLastUpdated() {
          return lastUpdated;
      }

      public void setLastUpdated(Timestamp lastUpdated) {
          this.lastUpdated = lastUpdated;
      }

      public String getJobConfigurationLocation() {
          return jobConfigurationLocation;
      }

      public void setJobConfigurationLocation(String jobConfigurationLocation) {
          this.jobConfigurationLocation = jobConfigurationLocation;
      }

      public List<BatchJobExecutionParams> getJobExecutionParams() {
          return jobExecutionParams;
      }

      public void setJobExecutionParams(List<BatchJobExecutionParams> jobExecutionParams) {
          this.jobExecutionParams = jobExecutionParams;
      }

      public List<BatchStepExecution> getStepExecutions() {
          return stepExecutions;
      }

      public void setStepExecutions(List<BatchStepExecution> stepExecutions) {
          this.stepExecutions = stepExecutions;
      }
  }
```

## Create a repository class that extends AbstractQueryDslRepository<BatchJobInstance>

```java
    @Repository
    public class BatchJobInstanceRepository extends AbstractQueryDslRepository<BatchJobInstance> {

      public BatchJobInstanceRepository(SQLQueryFactory sqlQueryFactory) throws Exception {
          super(sqlQueryFactory, BatchJobInstance.class);
      }
  }
```