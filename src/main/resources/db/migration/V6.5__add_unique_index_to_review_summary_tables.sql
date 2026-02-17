CREATE UNIQUE INDEX ux_product_review_summary_external
    ON public.product_review_summary USING btree (external_product_id);

CREATE UNIQUE INDEX ux_product_review_keywords_external
    ON public.product_review_keywords USING btree (external_product_id);

CREATE UNIQUE INDEX ux_ai_review_summary_external
    ON public.ai_review_summary USING btree (external_product_id);