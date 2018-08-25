ALTER TABLE trait_action DROP CONSTRAINT trait_action_actionbar_item_id_fkey;
ALTER TABLE trait_action ADD CONSTRAINT trait_action_actionbar_item_id_fkey FOREIGN KEY (actionbar_item_id) REFERENCES actionbar_item (actionbar_item_id) ON DELETE CASCADE;

ALTER TABLE item_action DROP CONSTRAINT item_action_actionbar_item_id_fkey;
ALTER TABLE item_action ADD CONSTRAINT item_action_actionbar_item_id_fkey FOREIGN KEY (actionbar_item_id) REFERENCES actionbar_item (actionbar_item_id) ON DELETE CASCADE;