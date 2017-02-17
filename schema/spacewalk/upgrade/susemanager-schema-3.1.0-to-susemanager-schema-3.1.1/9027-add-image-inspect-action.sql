--
-- Copyright (c) 2017 SUSE LLC
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

CREATE TABLE rhnActionImageInspect
(
    id               NUMBER NOT NULL
                         CONSTRAINT rhn_act_image_inspect_id_pk PRIMARY KEY,
    action_id        NUMBER NOT NULL
                         CONSTRAINT rhn_act_image_inspect_act_fk
                             REFERENCES rhnAction (id)
                             ON DELETE CASCADE,
    image_store_id NUMBER NOT NULL
                         CONSTRAINT rhn_act_image_inspect_is_fk
                             REFERENCES suseImageStore (id)
                             ON DELETE CASCADE,
    tag              VARCHAR2(128),
    name             VARCHAR2(128),
    created          timestamp with local time zone
                         DEFAULT (current_timestamp) NOT NULL,
    modified         timestamp with local time zone
                         DEFAULT (current_timestamp) NOT NULL
)
ENABLE ROW MOVEMENT
;

CREATE UNIQUE INDEX rhn_act_image_inspect_aid_idx
    ON rhnActionImageInspect (action_id)
    NOLOGGING;

CREATE SEQUENCE rhn_act_image_inspect_id_seq;


CREATE TABLE rhnActionImageInspectResult
(
    server_id              NUMBER NOT NULL
                               CONSTRAINT rhn_image_inspect_result_sid_fk
                                   REFERENCES rhnServer (id)
                                   ON DELETE CASCADE,
    action_image_inspect_id NUMBER NOT NULL
                               CONSTRAINT rhn_image_inspect_result_aid_fk
                                   REFERENCES rhnActionImageInspect (id)
                                   ON DELETE CASCADE
)
ENABLE ROW MOVEMENT
;

CREATE UNIQUE INDEX rhn_image_inspect_result_sa_uq
    ON rhnActionImageInspectResult (server_id, action_image_inspect_id);

CREATE INDEX rhn_image_inspect_result_ad_idx
    ON rhnActionImageInspectResult (action_image_inspect_id)
    NOLOGGING;

insert into rhnActionType values (505, 'image.inspect', 'Inspect an Image', 'N', 'N');

