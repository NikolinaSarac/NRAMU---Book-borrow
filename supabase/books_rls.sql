ALTER TABLE books ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public read access for books"
ON books
FOR SELECT
USING (true);

CREATE POLICY "Users can insert their own books"
ON books
FOR INSERT
WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own books"
ON books
FOR UPDATE
USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own books"
ON books
FOR DELETE
USING (auth.uid() = user_id);
