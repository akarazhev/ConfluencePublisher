# Prompt 12: Frontend Schedules Component

## Role

You are an expert front-end engineer.

## Task

Create the Schedules page component that displays all publication schedules with auto-refresh.

## Location

`src/app/pages/schedules/schedules.component.ts`

## Component Configuration

- Standalone component
- Imports: CommonModule
- ChangeDetection: OnPush
- Implements: OnInit, OnDestroy
- Inline template

## State (using Angular Signals)

| Signal | Type       | Initial |
|--------|------------|---------|
| rows   | Schedule[] | []      |
| busy   | boolean    | false   |

## Lifecycle

**ngOnInit**:

- Call load() immediately
- Set up interval to call load() every 5 seconds
- Check `typeof window !== 'undefined'` for SSR safety

**ngOnDestroy**:

- Clear the interval

## Template Structure

**Header Row**:

- Title: "Publication Schedules"
- Refresh button (disabled when busy)

**Data Table**:

- Columns: ID, Page ID, Status, Scheduled, Attempts, Error
- Status column with color coding: green for "posted", yellow for "queued", red for "failed"
- Error column shows lastError (truncated if long, with tooltip for full text)
- Alternating row colors using `$even`
- Horizontal scroll on overflow

## Methods

| Method                 | Description                                   |
|------------------------|-----------------------------------------------|
| load()                 | Fetch schedules from API, update rows         |
| formatDate(dateString) | Convert ISO string to localized date          |
| getStatusClass(status) | Return TailwindCSS classes for status badge   |
| truncateError(error)   | Truncate error message to 30 chars with "..." |

## Table Layout

```
┌────┬─────────┬────────┬─────────────────┬──────────┬─────────────────┐
│ ID │ Page ID │ Status │ Scheduled       │ Attempts │ Error           │
├────┼─────────┼────────┼─────────────────┼──────────┼─────────────────┤
│ 1  │ 5       │ posted │ 12/8/24, 3:00PM │ 1        │ -               │
│ 2  │ 6       │ failed │ 12/8/24, 4:00PM │ 3        │ Connection err… │
│ 3  │ 7       │ queued │ 12/8/24, 5:00PM │ 0        │ -               │
└────┴─────────┴────────┴─────────────────┴──────────┴─────────────────┘
```

## Styling (TailwindCSS)

- White background table with border
- Gray header row
- Alternating row backgrounds
- Padding on cells
- Border bottom on cells
- Status badges: `bg-green-100 text-green-800` for posted, `bg-yellow-100 text-yellow-800` for queued,
  `bg-red-100 text-red-800` for failed
- Error text in `text-red-600` with `truncate` class and `title` attribute for tooltip

## Status Values

- **queued**: Waiting to be processed
- **posted**: Successfully published
- **failed**: Publication failed

## Verification Criteria

- Table loads on initialization
- Auto-refresh every 5 seconds
- Manual refresh button works
- Dates formatted correctly
- Alternating row colors
- No memory leaks (interval cleared)
