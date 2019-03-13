package org.bahmni.batch;

import org.bahmni.batch.helper.FreeMarkerEvaluator;
import org.bahmni.batch.report.JobResult;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ReportGenerator {

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private FreeMarkerEvaluator<List<JobResult>> evaluator;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

	public String generateReport(){
		List<String> jobNames = jobExplorer.getJobNames();
		List<JobInstance> jobInstanceList = new ArrayList<>();
		for (String jobName : jobNames) {
			jobInstanceList.addAll(jobExplorer.findJobInstancesByJobName(jobName, 0, 20));
		}
		List<JobResult> results = transformJobExecutionsToReport(getJobExecutionsForInstances(jobInstanceList));

		return evaluator.evaluate("report.ftl",results);
	}

	private List<JobExecution> getJobExecutionsForInstances(List<JobInstance> jobInstanceList){
		List<JobExecution> executions = new ArrayList<>();
		for(JobInstance jobInstance: jobInstanceList){
			executions.addAll(jobExplorer.getJobExecutions(jobInstance));
		}
		return executions;
	}

	private List<JobResult> transformJobExecutionsToReport(List<JobExecution> jobExecutions) {
		List<JobResult> jobResults = new ArrayList<>();
		sortAllJobExecutions(jobExecutions);
		for(JobExecution execution: jobExecutions){
			String zipFileName = execution.getExecutionContext().getString(JobCompletionNotificationListener.OUTPUT_FILE_NAME_CONTEXT_KEY);
			jobResults.add(new JobResult(dateFormat.format(execution.getCreateTime()),
					execution.getExitStatus().getExitCode(), zipFileName, execution.getJobInstance().getJobName()));
		}

		return jobResults;
	}

	private void sortAllJobExecutions(List<JobExecution> jobExecutions) {
		jobExecutions.sort(new Comparator<JobExecution>() {
			@Override
			public int compare(JobExecution o1, JobExecution o2) {
				return o2.getCreateTime().compareTo(o1.getCreateTime());
			}
		});
	}

	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}

	public void setEvaluator(FreeMarkerEvaluator<List<JobResult>> evaluator) {
		this.evaluator = evaluator;
	}
}
