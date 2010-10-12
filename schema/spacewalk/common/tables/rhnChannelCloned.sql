--
-- Copyright (c) 2008--2010 Red Hat, Inc.
--
-- This software is licensed to you under the GNU General Public License,
-- version 2 (GPLv2). There is NO WARRANTY for this software, express or
-- implied, including the implied warranties of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
-- along with this software; if not, see
-- http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
--
-- Red Hat trademarks are not licensed under GPLv2. No permission is
-- granted to use or replicate Red Hat trademarks that are incorporated
-- in this software or its documentation.
--


CREATE TABLE rhnChannelCloned
(
    original_id  NUMBER NOT NULL
                     CONSTRAINT rhn_channelclone_fcid_fk
                         REFERENCES rhnChannel (id)
                         ON DELETE CASCADE,
    id           NUMBER NOT NULL
                     CONSTRAINT rhn_channelclone_tcid_fk
                         REFERENCES rhnChannel (id)
                         ON DELETE CASCADE,
    created      DATE
                     DEFAULT (sysdate) NOT NULL,
    modified     DATE
                     DEFAULT (sysdate) NOT NULL
)
ENABLE ROW MOVEMENT
;

CREATE INDEX rhn_channelclone_fcid_tcid_idx
    ON rhnChannelCloned (original_id, id)
    TABLESPACE [[2m_tbs]];

ALTER TABLE rhnChannelCloned
    ADD CONSTRAINT rhn_channelclone_fcid_tcid_uq UNIQUE (original_id, id);

ALTER TABLE rhnChannelCloned
    ADD CONSTRAINT rhn_channelclone_id_pk PRIMARY KEY (id);

