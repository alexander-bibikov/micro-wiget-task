DROP TABLE IF EXISTS widgets;

CREATE TABLE widgets (
  widget_id    VARCHAR(200) NOT NULL,
  widget_name  VARCHAR(200) NOT NULL,
  coordinate_x BIGINT       NOT NULL,
  coordinate_y BIGINT       NOT NULL,
  coordinate_z BIGINT,
  width        BIGINT       NOT NULL,
  height       BIGINT       NOT NULL,
  updated_at   BIGINT       NOT NULL,
  created_at   BIGINT       NOT NULL,
  PRIMARY KEY (widget_id)
);