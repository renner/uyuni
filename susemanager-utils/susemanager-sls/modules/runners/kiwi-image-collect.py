# SUSE Manager
# Copyright (c) 2018 SUSE LLC

# runner to collect image from build host

import os
import salt.exceptions
import logging

log = logging.getLogger(__name__)

def upload_file_from_minion(hostname, filetoupload, targetdir):
    src = 'root@' + hostname + ':' + filetoupload
    return __salt__['salt.cmd'](
      'rsync.rsync',
      src, targetdir,
      rsh='ssh -o IdentityFile=/srv/susemanager/salt/salt_ssh/mgr_ssh_id -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null'
    )

def move_file_from_minion_cache(minion, filetomove, targetdir):
    src = os.path.join(__opts__['cachedir'], 'minions', minion, 'files', filetomove.lstrip('/'))
    return __salt__['salt.cmd']('file.move', src, targetdir);

def kiwi_collect_image(minion, filepath, image_store_dir):
    __salt__['salt.cmd']('file.mkdir', image_store_dir)

    pillars = list(__salt__['cache.pillar'](tgt=minion).values())[0]
    if pillars.get('use_salt_transport'):
      log.info('Collecting image "{}" from minion cache'.format(filepath))
      return move_file_from_minion_cache(minion, filepath, image_store_dir)

    log.info('Collecting image "{}" from minion using rsync'.format(filepath))
    return upload_file_from_minion(minion, filepath, image_store_dir)
