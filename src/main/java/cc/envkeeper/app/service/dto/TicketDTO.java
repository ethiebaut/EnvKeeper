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

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import javax.persistence.Lob;
import cc.envkeeper.app.domain.enumeration.TicketType;

/**
 * A DTO for the {@link cc.envkeeper.app.domain.Ticket} entity.
 */
public class TicketDTO implements Serializable {

    private Long id;

    @NotNull
    private String externalId;

    @NotNull
    private TicketType ticketType;

    @NotNull
    private String summary;

    private String ticketUrl;

    private String priority;

    private String status;

    @NotNull
    private Instant created;

    @NotNull
    private Instant updated;

    private String reporter;

    private String assignee;

    @Lob
    private String description;


    private Long affectsId;

    private String affectsVersion;

    private Long fixedInId;

    private String fixedInVersion;

    private Long affectsProductId;

    private String affectsProductShortname;

    private Long fixedInProductId;

    private String fixedInProductShortname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAffectsId() {
        return affectsId;
    }

    public void setAffectsId(Long productVersionId) {
        this.affectsId = productVersionId;
    }

    public String getAffectsVersion() {
        return affectsVersion;
    }

    public void setAffectsVersion(String productVersionVersion) {
        this.affectsVersion = productVersionVersion;
    }

    public Long getFixedInId() {
        return fixedInId;
    }

    public void setFixedInId(Long productVersionId) {
        this.fixedInId = productVersionId;
    }

    public String getFixedInVersion() {
        return fixedInVersion;
    }

    public void setFixedInVersion(String productVersionVersion) {
        this.fixedInVersion = productVersionVersion;
    }

    public Long getAffectsProductId() {
        return affectsProductId;
    }

    public void setAffectsProductId(final Long affectsProductId) {
        this.affectsProductId = affectsProductId;
    }

    public String getAffectsProductShortname() {
        return affectsProductShortname;
    }

    public void setAffectsProductShortname(final String affectsProductShortname) {
        this.affectsProductShortname = affectsProductShortname;
    }

    public Long getFixedInProductId() {
        return fixedInProductId;
    }

    public void setFixedInProductId(final Long fixedInProductId) {
        this.fixedInProductId = fixedInProductId;
    }

    public String getFixedInProductShortname() {
        return fixedInProductShortname;
    }

    public void setFixedInProductShortname(final String fixedInProductShortname) {
        this.fixedInProductShortname = fixedInProductShortname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketDTO)) {
            return false;
        }

        return id != null && id.equals(((TicketDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketDTO{" +
            "id=" + getId() +
            ", externalId='" + getExternalId() + "'" +
            ", ticketType='" + getTicketType() + "'" +
            ", summary='" + getSummary() + "'" +
            ", ticketUrl='" + getTicketUrl() + "'" +
            ", priority='" + getPriority() + "'" +
            ", status='" + getStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", reporter='" + getReporter() + "'" +
            ", assignee='" + getAssignee() + "'" +
            ", description='" + getDescription() + "'" +
            ", affectsId=" + getAffectsId() +
            ", affectsVersion='" + getAffectsVersion() + "'" +
            ", fixedInId=" + getFixedInId() +
            ", fixedInVersion='" + getFixedInVersion() + "'" +
            "}";
    }
}
