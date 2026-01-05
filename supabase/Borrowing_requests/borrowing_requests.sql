create table borrowing_requests (
    id uuid primary key default gen_random_uuid(),
    book_id uuid references books(id) on delete cascade,
    requester_id uuid references auth.users(id) on delete cascade,
    owner_id uuid references auth.users(id) on delete cascade,
    status text default 'pending',
    created_at timestamp with time zone default now()
);