package de.zeus.hermes.model;

import lombok.Data;
import java.util.List;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Represents a workflow consisting of a sequence of {@link WorkflowStep} instances.
 *
 * <p>
 * This class encapsulates the steps that define a workflow, which are executed sequentially
 * by {@link de.zeus.hermes.service.WorkflowEngine}. It serves as the primary structure for
 * managing workflow execution logic.
 * </p>
 *
 * @version 1.0.1
 */
@Data
public class Workflow {

	/**
	 * The list of steps that make up this workflow.
	 * Each step is executed sequentially during workflow execution.
	 */
	private List<WorkflowStep> steps;

	/**
	 * Default constructor initializing an empty workflow.
	 * The steps list must be set separately using {@link #setSteps(List)}.
	 */
	public Workflow() {
		// No initialization needed; Lombok handles field defaults
	}

	/**
	 * Constructor initializing the workflow with a list of steps.
	 *
	 * @param steps The list of {@link WorkflowStep} instances to execute. Can be null or empty,
	 *              but execution will be skipped if not populated.
	 */
	public Workflow(List<WorkflowStep> steps) {
		this.steps = steps; // Assign the provided steps directly
	}
}