/*
 * Copyright (c) 2022 Eric Thiebaut-George.
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
 *
 */

package cc.envkeeper.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import cc.envkeeper.app.domain.enumeration.TicketType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link cc.envkeeper.app.domain.Ticket} entity. This class is used
 * in {@link cc.envkeeper.app.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TicketCriteria implements Serializable, Criteria {
    /**
     * Class for filtering TicketType
     */
    public static class TicketTypeFilter extends Filter<TicketType> {

        public TicketTypeFilter() {
        }

        public TicketTypeFilter(TicketTypeFilter filter) {
            super(filter);
        }

        @Override
        public TicketTypeFilter copy() {
            return new TicketTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter externalId;

    private TicketTypeFilter ticketType;

    private StringFilter summary;

    private StringFilter ticketUrl;

    private StringFilter priority;

    private StringFilter status;

    private InstantFilter created;

    private InstantFilter updated;

    private StringFilter reporter;

    private StringFilter assignee;

    private LongFilter affectsId;

    private LongFilter fixedInId;

    public TicketCriteria() {
    }

    public TicketCriteria(TicketCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.externalId = other.externalId == null ? null : other.externalId.copy();
        this.ticketType = other.ticketType == null ? null : other.ticketType.copy();
        this.summary = other.summary == null ? null : other.summary.copy();
        this.ticketUrl = other.ticketUrl == null ? null : other.ticketUrl.copy();
        this.priority = other.priority == null ? null : other.priority.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.updated = other.updated == null ? null : other.updated.copy();
        this.reporter = other.reporter == null ? null : other.reporter.copy();
        this.assignee = other.assignee == null ? null : other.assignee.copy();
        this.affectsId = other.affectsId == null ? null : other.affectsId.copy();
        this.fixedInId = other.fixedInId == null ? null : other.fixedInId.copy();
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getExternalId() {
        return externalId;
    }

    public void setExternalId(StringFilter externalId) {
        this.externalId = externalId;
    }

    public TicketTypeFilter getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketTypeFilter ticketType) {
        this.ticketType = ticketType;
    }

    public StringFilter getSummary() {
        return summary;
    }

    public void setSummary(StringFilter summary) {
        this.summary = summary;
    }

    public StringFilter getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(StringFilter ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public StringFilter getPriority() {
        return priority;
    }

    public void setPriority(StringFilter priority) {
        this.priority = priority;
    }

    public StringFilter getStatus() {
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public InstantFilter getCreated() {
        return created;
    }

    public void setCreated(InstantFilter created) {
        this.created = created;
    }

    public InstantFilter getUpdated() {
        return updated;
    }

    public void setUpdated(InstantFilter updated) {
        this.updated = updated;
    }

    public StringFilter getReporter() {
        return reporter;
    }

    public void setReporter(StringFilter reporter) {
        this.reporter = reporter;
    }

    public StringFilter getAssignee() {
        return assignee;
    }

    public void setAssignee(StringFilter assignee) {
        this.assignee = assignee;
    }

    public LongFilter getAffectsId() {
        return affectsId;
    }

    public void setAffectsId(LongFilter affectsId) {
        this.affectsId = affectsId;
    }

    public LongFilter getFixedInId() {
        return fixedInId;
    }

    public void setFixedInId(LongFilter fixedInId) {
        this.fixedInId = fixedInId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketCriteria that = (TicketCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(externalId, that.externalId) &&
            Objects.equals(ticketType, that.ticketType) &&
            Objects.equals(summary, that.summary) &&
            Objects.equals(ticketUrl, that.ticketUrl) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(status, that.status) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated) &&
            Objects.equals(reporter, that.reporter) &&
            Objects.equals(assignee, that.assignee) &&
            Objects.equals(affectsId, that.affectsId) &&
            Objects.equals(fixedInId, that.fixedInId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        externalId,
        ticketType,
        summary,
        ticketUrl,
        priority,
        status,
        created,
        updated,
        reporter,
        assignee,
        affectsId,
        fixedInId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (externalId != null ? "externalId=" + externalId + ", " : "") +
                (ticketType != null ? "ticketType=" + ticketType + ", " : "") +
                (summary != null ? "summary=" + summary + ", " : "") +
                (ticketUrl != null ? "ticketUrl=" + ticketUrl + ", " : "") +
                (priority != null ? "priority=" + priority + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (created != null ? "created=" + created + ", " : "") +
                (updated != null ? "updated=" + updated + ", " : "") +
                (reporter != null ? "reporter=" + reporter + ", " : "") +
                (assignee != null ? "assignee=" + assignee + ", " : "") +
                (affectsId != null ? "affectsId=" + affectsId + ", " : "") +
                (fixedInId != null ? "fixedInId=" + fixedInId + ", " : "") +
            "}";
    }

}
