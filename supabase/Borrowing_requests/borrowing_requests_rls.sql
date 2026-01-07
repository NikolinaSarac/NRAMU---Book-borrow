alter table borrowing_requests enable row level security;

-- Korisnici mogu vidjeti samo svoje zahtjeve (requester_id)
create policy "Users can view own requests"
on borrowing_requests
for select
using (auth.uid() = requester_id);

-- Korisnici mogu dodavati samo svoje zahtjeve
create policy "Users can insert own requests"
on borrowing_requests
for insert
with check (auth.uid() = requester_id);

-- Korisnici mogu mijenjati status svojih zahtjeva
create policy "Users can update own requests"
on borrowing_requests
for update
using (auth.uid() = requester_id);

