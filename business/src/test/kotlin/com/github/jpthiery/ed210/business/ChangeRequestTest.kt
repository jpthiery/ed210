package com.github.jpthiery.ed210.business

import com.github.jpthiery.ed210.business.FailedDecideResultExpected.Companion.failedWithoutCheckedReason
import com.github.jpthiery.ed210.business.NoopDecideResultExpected.Companion.commandNoop
import com.github.jpthiery.ed210.business.SuccessDecideResultExpected.Companion.commandSucceeded
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/*
   Copyright 2020 Jean-Pascal Thiery

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

internal class ChangeRequestTest {

    @TestFactory
    fun `Decide on ChangeRequest`() = listOf(
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            CreateChangeRequest(
                                    defaultChangeRequestId,
                                    fakeMrUrl,
                                    defaultGitContext,
                                    ScmSupported.GITLAB
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCreated::class)),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            PushCode(
                                    defaultChangeRequestId
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            RequestPlan(
                                    defaultChangeRequestId
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Success Plan",
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            RequestApply(
                                    defaultChangeRequestId
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            SubmitApplyResult(
                                    defaultChangeRequestId,
                                    "Successfully Applied",
                            true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            RequestMerge(
                                    defaultChangeRequestId
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            PushCode(defaultChangeRequestId)
                    )
                    .then(commandSucceeded(ChangeRequestCodePushed::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            RequestPlan(defaultChangeRequestId)
                    )
                    .then(commandSucceeded(PlanRequested::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Successful planned.",
                                    true
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodePlanned::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Failed to plan.",
                                    false
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodePlanned::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Failed to plan.",
                                    false
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodePlanned::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            RequestApply(defaultChangeRequestId)
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            SubmitApplyResult(
                                    defaultChangeRequestId,
                                    "Apply output result",
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            RequestMerge(defaultChangeRequestId)
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Not Ansible playbook found.",
                                    false
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodePlanned::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            SubmitPlanResult(
                                    defaultChangeRequestId,
                                    "Ansible playbook found.",
                                    true
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodePlanned::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            RequestPlan(defaultChangeRequestId)
                    )
                    .then(commandSucceeded(PlanRequested::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            RequestApply(defaultChangeRequestId)
                    )
                    .then(commandSucceeded(ApplyRequested::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            SubmitApplyResult(
                                    defaultChangeRequestId,
                                    "Success apply",
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            RequestMerge(defaultChangeRequestId)
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplying)
                    .whenApplyCommand(
                            RequestApply(
                                    defaultChangeRequestId
                            )
                    )
                    .then(commandNoop()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplying)
                    .whenApplyCommand(
                            SubmitApplyResult(
                                    defaultChangeRequestId,
                                    "Unable to run Ansible.",
                                    false
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodeApplied::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplying)
                    .whenApplyCommand(
                            SubmitApplyResult(
                                    defaultChangeRequestId,
                                    "Run Ansible successfully.",
                                    true
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodeApplied::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplying)
                    .whenApplyCommand(
                            RequestMerge(defaultChangeRequestId)
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestApplying)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    true
                            )
                    )
                    .then(failedWithoutCheckedReason()),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestMerging)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    true
                            )
                    )
                    .then(commandSucceeded(ChangeRequestCodeMerged::class)),
            decideTestOnChangeRequestWith()
                    .given(defaultChangeRequestMerging)
                    .whenApplyCommand(
                            SubmitMergeResult(
                                    defaultChangeRequestId,
                                    false
                            )
                    )
                    .then(commandNoop()),
            decideTestOnChangeRequestWith()
                    .given(defaulChangeRequestClosed)
                    .whenApplyCommand(
                            CreateChangeRequest(
                                    defaultChangeRequestId,
                                    fakeMrUrl,
                                    defaultGitContext,
                                    ScmSupported.GITLAB
                            )
                    )
                    .then(failedWithoutCheckedReason())
    ).map {
        DynamicTest.dynamicTest(
                "When ${it.command::class.simpleName} on change request state ${it.initialState::class.simpleName} then expecting ${it.expectedResult}",
                assertOnDecideFixture(it, ChangeRequest())
        )
    }

    @TestFactory
    fun `Apply on ChangeRequest`() = listOf(
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            ChangeRequestCreated(
                                    defaultChangeRequestId,
                                    fakeMrUrl,
                                    defaultGitContext,
                                    ScmSupported.GITLAB
                            )
                    )
                    .then(defaultChangeRequestWIP),
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            ChangeRequestCodePushed(
                                    defaultChangeRequestId
                            )
                    )
                    .then(ChangeRequestNotExist),
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            PlanRequested(
                                    defaultChangeRequestId
                            )
                    )
                    .then(ChangeRequestNotExist),
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            ChangeRequestCodePlanned(
                                    defaultChangeRequestId,
                                    "Successfully plan",
                                    true
                            )
                    )
                    .then(ChangeRequestNotExist),
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            ApplyRequested(
                                    defaultChangeRequestId
                            )
                    )
                    .then(ChangeRequestNotExist),
            applyTestOnRequestChangeWith()
                    .given(ChangeRequestNotExist)
                    .whenApplyEvent(
                            ChangeRequestCodeApplied(
                                    defaultChangeRequestId,
                                    "Successfully applied",
                                    true
                            )
                    )
                    .then(ChangeRequestNotExist),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyEvent(
                            PlanRequested(defaultChangeRequestId)
                    )
                    .then(defaultChangeRequestWIP),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyEvent(
                            ChangeRequestCodePlanned(
                                    defaultChangeRequestId,
                                    "Fail to plan",
                                    false
                            )
                    )
                    .then(defaultChangeRequestWIP),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestWIP)
                    .whenApplyEvent(
                            ChangeRequestCodePlanned(
                                    defaultChangeRequestId,
                                    "Plan success",
                                    true
                            )
                    )
                    .then(defaultChangeRequestApplyable),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyEvent(
                            ChangeRequestCodePushed(defaultChangeRequestId)
                    )
                    .then(defaultChangeRequestWIP),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyEvent(
                            ChangeRequestCodeApplied(
                                    defaultChangeRequestId,
                                    "Successfully applied",
                                    true
                            )
                    )
                    .then(defaultChangeRequestMerging.copy(outputApply = "Successfully applied")),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestApplyable)
                    .whenApplyEvent(
                            ChangeRequestCodeApplied(
                                    defaultChangeRequestId,
                                    "Failed to apply",
                                    false
                            )
                    )
                    .then(defaultChangeRequestWIP),
            applyTestOnRequestChangeWith()
                    .given(defaultChangeRequestMerging)
                    .whenApplyEvent(
                            ChangeRequestCodeMerged(defaultChangeRequestId)
                    )
                    .then(ChangeRequestClosed(defaultChangeRequestId))

    ).map {
        DynamicTest.dynamicTest(
                "Given project state ${it.initialState::class}, When applying event ${it.eventToApply}, Then expecting to get state ${it.expectedState::class}",
                assertOnApply(it, ChangeRequest())
        )
    }

}


//  Alias
fun decideTestOnChangeRequestWith(): DecideStateAppender<ChangeRequestCommand, ChangeRequestState, ChangeRequestEvent> = decideTestWith()

fun applyTestOnRequestChangeWith(): ApplierStateAppender<ChangeRequestState, ChangeRequestEvent> = applierTestWith()

const val defaultChangeRequestName = "MR Title"
val fakeMrUrl = "git@fake.com/t/merge_request/!1"
val fakeGitUrl = "git@fake.com/t/u.git"
val sourceRef = "srcBranche"
val targetRef = "destBranche"
val defaultChangeRequestId = ChangeRequestId.from(
        fakeMrUrl
)

val defaultGitContext = GitChangeContext(
        fakeGitUrl,
        sourceRef,
        targetRef
)

val defaultChangeRequestWIP = ChangeRequestInProgress(
        defaultChangeRequestId,
        fakeMrUrl,
        defaultGitContext,
        ScmSupported.GITLAB,
        listOf()
)


val defaultChangeRequestApplyable = ChangeRequestApplyable(
        defaultChangeRequestId,
        fakeMrUrl,
        defaultGitContext,
        ScmSupported.GITLAB,
        listOf()
)

val defaultChangeRequestApplying = ChangeRequestApplying(
        defaultChangeRequestId,
        fakeMrUrl,
        defaultGitContext,
        ScmSupported.GITLAB,
        listOf()
)

val defaultChangeRequestMerging = ChangeRequestMerging(
        defaultChangeRequestId,
        fakeMrUrl,
        ScmSupported.GITLAB,
        listOf(),
        ""
)

val defaulChangeRequestClosed = ChangeRequestClosed(
        defaultChangeRequestId
)
