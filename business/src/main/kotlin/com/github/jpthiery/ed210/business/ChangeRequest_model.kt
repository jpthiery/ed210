package com.github.jpthiery.ed210.business

import java.security.MessageDigest
import java.time.Clock

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

data class ChangeRequestId(val id: String) : StreamId {
    companion object {
        fun from(changeRequestUrl: String): ChangeRequestId {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val id = messageDigest.digest(changeRequestUrl.toByteArray())
            return ChangeRequestId(id.toString())
        }
    }
}

sealed class ChangeRequestCommand : Command {
    abstract val id: ChangeRequestId
    override fun id(): StreamId = id
}

data class CreateChangeRequest(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val gitChangeContext: GitChangeContext,
        val sourceScmType: ScmSupported
) : ChangeRequestCommand()

data class PushCode(
        override val id: ChangeRequestId
) : ChangeRequestCommand()

data class RequestPlan(
        override val id: ChangeRequestId
) : ChangeRequestCommand()

data class SubmitPlanResult(
        override val id: ChangeRequestId,
        val outputPlan: String,
        val success: Boolean
) : ChangeRequestCommand()

data class RequestApply(
        override val id: ChangeRequestId
) : ChangeRequestCommand()

data class SubmitApplyResult(
        override val id: ChangeRequestId,
        val outputApply: String,
        val success: Boolean
) : ChangeRequestCommand()

data class RequestMerge(
        override val id: ChangeRequestId
) : ChangeRequestCommand()

data class SubmitMergeResult(
        override val id: ChangeRequestId,
        val success: Boolean
) : ChangeRequestCommand()

sealed class ChangeRequestEvent(clock: Clock = Clock.systemUTC()) : Event {
    abstract val id: ChangeRequestId
    private val happenedDate: Long = clock.millis()
    override fun id(): ChangeRequestId = id
    override fun happenedDate(): Long = happenedDate
}

data class ChangeRequestCreated(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val gitChangeContext: GitChangeContext,
        val sourceScmType: ScmSupported
) : ChangeRequestEvent()

data class ChangeRequestCodePushed(
        override val id: ChangeRequestId
) : ChangeRequestEvent()

data class PlanRequested(
        override val id: ChangeRequestId
) : ChangeRequestEvent()

data class ChangeRequestCodePlanned(
        override val id: ChangeRequestId,
        val outputPlan: String,
        val success: Boolean
) : ChangeRequestEvent()

data class ApplyRequested(
        override val id: ChangeRequestId
) : ChangeRequestEvent()

data class ChangeRequestCodeApplied(
        override val id: ChangeRequestId,
        val outputApplied: String,
        val success: Boolean
) : ChangeRequestEvent()

data class ChangeRequestCodeMerged(
        override val id: ChangeRequestId
) : ChangeRequestEvent()

sealed class ChangeRequestState : State {
    abstract val id: ChangeRequestId
    override fun id(): StreamId = id
}

data class ChangeRequestInProgress(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val gitChangeContext: GitChangeContext,
        val sourceScmType: ScmSupported,
        val scmRequests: List<ScmRequest>
) : ChangeRequestState()

data class ChangeRequestApplyable(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val gitChangeContext: GitChangeContext,
        val sourceScmType: ScmSupported,
        val scmRequests: List<ScmRequest>
) : ChangeRequestState()

data class ChangeRequestApplying(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val gitChangeContext: GitChangeContext,
        val sourceScmType: ScmSupported,
        val scmRequests: List<ScmRequest>
) : ChangeRequestState()

data class ChangeRequestMerging(
        override val id: ChangeRequestId,
        val changeRequestUrl: String,
        val sourceScmType: ScmSupported,
        val scmRequests: List<ScmRequest>,
        val outputApply: String
) : ChangeRequestState()

data class ChangeRequestClosed(
        override val id: ChangeRequestId
) : ChangeRequestState()

object ChangeRequestNotExist : ChangeRequestState() {
    override val id: ChangeRequestId
        get() = ChangeRequestId("Unknown")
}

data class ScmRequest(
        val scmType: ScmSupported,
        val requestDate: Long,
        val rawRequest: ScmRawRequest
)

data class ScmRawRequest(
        val rawBody: String,
        val rawHeaders: Map<String, String>
)

data class GitChangeContext(
        val repositoryUrl: String,
        val sourceBranchRef: String,
        val targetBranchRef: String
)

enum class ScmSupported {
    GITLAB
}