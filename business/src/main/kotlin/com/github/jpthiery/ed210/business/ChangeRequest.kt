package com.github.jpthiery.ed210.business

import arrow.core.Either

typealias DecideResult = Either<String, List<ChangeRequestEvent>>

class ChangeRequest : Aggregate<ChangeRequestCommand, ChangeRequestState, ChangeRequestEvent> {

    override fun decide(command: ChangeRequestCommand, state: ChangeRequestState): DecideResult =
            when (state) {
                is ChangeRequestNotExist -> decideOnNotExist(command)
                is ChangeRequestInProgress -> decideOnWip(command, state)
                is ChangeRequestApplying -> decideOnApplying(command, state)
                is ChangeRequestApplyable -> decideOnApplyable(command, state)
                is ChangeRequestMerging -> decideOnMerging(command, state)
                is ChangeRequestClosed -> Either.left("This change request is already closed.")
            }

    override fun apply(state: ChangeRequestState, event: ChangeRequestEvent): ChangeRequestState =
            when (state) {
                is ChangeRequestNotExist -> applyOnNotExist(event)
                is ChangeRequestInProgress -> applyOnWorkInProgress(state, event)
                is ChangeRequestApplyable -> applyOnApplyable(state, event)
                is ChangeRequestApplying -> applyOnApplying(state, event)
                is ChangeRequestMerging -> applyOnMerging(state, event)
                is ChangeRequestClosed -> state
            }

    private fun decideOnNotExist(command: ChangeRequestCommand): DecideResult =
            when (command) {
                is CreateChangeRequest -> Either.right(
                        listOf(
                                ChangeRequestCreated(
                                        command.id,
                                        command.changeRequestUrl,
                                        command.gitChangeContext,
                                        command.sourceScmType
                                )
                        )
                )
                else -> Either.left(
                        "Not able to decide on ChangeRequest not exist with command ${command::class.java}."
                )
            }

    private fun decideOnWip(command: ChangeRequestCommand, state: ChangeRequestInProgress): DecideResult =
            when (command) {
                is CreateChangeRequest -> Either.left("Change request already exist.")
                is PushCode -> successfullyDecide(ChangeRequestCodePushed(state.id))
                is RequestPlan -> successfullyDecide(PlanRequested(state.id))
                is SubmitPlanResult -> successfullyDecide(
                        ChangeRequestCodePlanned(
                                state.id,
                                command.outputPlan,
                                command.success
                        )
                )
                is RequestApply -> Either.left("You must plan before apply.")
                is SubmitApplyResult -> Either.left("You must plan before apply.")
                is RequestMerge -> Either.left("You must plan and apply before merge.")
                is SubmitMergeResult -> Either.left("You must plan and apply before merge.")
            }

    private fun decideOnApplyable(command: ChangeRequestCommand, state: ChangeRequestApplyable): DecideResult =
            when (command) {
                is CreateChangeRequest -> Either.left("Change request already exist.")
                is PushCode -> successfullyDecide(ChangeRequestCodePushed(state.id))
                is RequestPlan -> successfullyDecide(PlanRequested(state.id))
                is SubmitPlanResult -> successfullyDecide(
                        ChangeRequestCodePlanned(
                                state.id,
                                command.outputPlan,
                                command.success
                        )
                )
                is RequestApply -> successfullyDecide(ApplyRequested(state.id))
                is SubmitApplyResult -> Either.left("You must apply before submit result.")
                is RequestMerge -> Either.left("You must apply before merge.")
                is SubmitMergeResult -> Either.left("You must apply before merge.")
            }

    private fun decideOnApplying(command: ChangeRequestCommand, state: ChangeRequestApplying): DecideResult =
            when (command) {
                is CreateChangeRequest -> Either.left("Change request already exist.")
                is PushCode -> Either.right(listOf())
                is RequestPlan -> Either.right(listOf())
                is SubmitPlanResult -> Either.right(listOf())
                is RequestApply -> Either.right(listOf())
                is SubmitApplyResult -> successfullyDecide(
                        ChangeRequestCodeApplied(
                                state.id,
                                command.outputApply,
                                command.success
                        )
                )
                is RequestMerge -> Either.left("You must apply before merge.")
                is SubmitMergeResult -> Either.left("You must apply before merge.")
            }

    private fun decideOnMerging(command: ChangeRequestCommand, state: ChangeRequestMerging): DecideResult =
            if (command is SubmitMergeResult && command.success) {
                successfullyDecide(
                        ChangeRequestCodeMerged(
                                state.id
                        )
                )
            } else {
                Either.right(emptyList())
            }

    private fun applyOnNotExist(event: ChangeRequestEvent): ChangeRequestState =
            when (event) {
                is ChangeRequestCreated -> ChangeRequestInProgress(
                        event.id,
                        event.changeRequestUrl,
                        event.gitChangeContext,
                        event.sourceScmType,
                        emptyList()
                )
                else -> ChangeRequestNotExist
            }

    private fun applyOnWorkInProgress(state: ChangeRequestInProgress, event: ChangeRequestEvent): ChangeRequestState =
            if (event is ChangeRequestCodePlanned && event.success) {
                ChangeRequestApplyable(
                        state.id,
                        state.changeRequestUrl,
                        state.gitChangeContext,
                        state.sourceScmType,
                        state.scmRequests
                )
            } else {
                state
            }

    private fun applyOnApplyable(state: ChangeRequestApplyable, event: ChangeRequestEvent): ChangeRequestState =
            when (event) {
                is ChangeRequestCreated -> state
                is ChangeRequestCodePushed -> ChangeRequestInProgress(
                        state.id,
                        state.changeRequestUrl,
                        state.gitChangeContext,
                        state.sourceScmType,
                        state.scmRequests
                )
                is PlanRequested -> state
                is ChangeRequestCodePlanned -> state
                is ApplyRequested -> state
                is ChangeRequestCodeApplied -> if (event.success) {
                    ChangeRequestMerging(
                            state.id,
                            state.changeRequestUrl,
                            state.sourceScmType,
                            state.scmRequests,
                            event.outputApplied
                    )
                } else {
                    ChangeRequestInProgress(
                            state.id,
                            state.changeRequestUrl,
                            state.gitChangeContext,
                            state.sourceScmType,
                            state.scmRequests
                    )
                }
                is ChangeRequestCodeMerged -> state
            }

    private fun applyOnApplying(state: ChangeRequestApplying, event: ChangeRequestEvent): ChangeRequestState =
            when (event) {
                is ChangeRequestCreated -> state
                is ChangeRequestCodePushed -> ChangeRequestInProgress(
                        state.id,
                        state.changeRequestUrl,
                        state.gitChangeContext,
                        state.sourceScmType,
                        state.scmRequests
                )
                is PlanRequested -> state
                is ChangeRequestCodePlanned -> if (event.success) {
                    state
                } else {
                    ChangeRequestInProgress(
                            state.id,
                            state.changeRequestUrl,
                            state.gitChangeContext,
                            state.sourceScmType,
                            state.scmRequests
                    )
                }
                is ApplyRequested -> state
                is ChangeRequestCodeApplied -> if (event.success) {
                    ChangeRequestMerging(
                            state.id,
                            state.changeRequestUrl,
                            state.sourceScmType,
                            state.scmRequests,
                            event.outputApplied
                    )
                } else {
                    ChangeRequestInProgress(
                            state.id,
                            state.changeRequestUrl,
                            state.gitChangeContext,
                            state.sourceScmType,
                            state.scmRequests
                    )
                }
                is ChangeRequestCodeMerged -> state
            }

    private fun applyOnMerging(state: ChangeRequestMerging, event: ChangeRequestEvent): ChangeRequestState =
            when (event) {
                is ChangeRequestCodeMerged -> ChangeRequestClosed(state.id)
                else -> state
            }

    override fun notExistState(): ChangeRequestState = ChangeRequestNotExist

    override fun getEventType(): Class<ChangeRequestEvent> = ChangeRequestEvent::class.java

    private fun successfullyDecide(event: ChangeRequestEvent): DecideResult = Either.right(
            listOf(
                    event
            )
    )

}

