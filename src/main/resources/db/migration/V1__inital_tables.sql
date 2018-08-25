CREATE TABLE actionbar_item (
  actionbar_item_id SERIAL PRIMARY KEY,
  username TEXT NOT NULL,
  index INTEGER NOT NULL
);

CREATE TABLE trait_action (
  trait_action_id SERIAL NOT NULL,
  actionbar_item_id INTEGER REFERENCES actionbar_item (actionbar_item_id),
  trait_name TEXT NOT NULL
);

CREATE TABLE item_action (
  item_action_id SERIAL NOT NULL,
  actionbar_item_id INTEGER REFERENCES actionbar_item (actionbar_item_id),
  item_name TEXT NOT NULL
);