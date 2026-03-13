---
name: finalize
description: Finalizes work by updating documentation and creating a git commit - triggered by "finalize" or "commit"
license: MIT
compatibility: opencode
metadata:
  audience: developers
  workflow: finalize
---

# Finalize Skill

This skill is triggered **only** when the user explicitly says "finalize" or "commit".

## Trigger Words

- `finalize`
- `commit`

**Do NOT run this skill automatically.** Wait for explicit user request.

## Workflow

When triggered, execute these steps in order:

### Step 1: Review Changes

```bash
git status
git diff --stat
```

Understand what was changed in this session.

### Step 2: Update Documentation (if needed)

Check if documentation needs updating based on the changes:

#### README.md
- New features → Add to Features section
- New configuration → Add to Configuration section
- New prerequisites → Add to Prerequisites

#### CONTEXT.md
- New/changed endpoints → Update Endpoints table
- New files/folders → Update Repository Structure
- New issues/solutions → Add to Known Issues

#### Code Comments
- Ensure all comments are in English
- Add JavaDoc to new public methods

#### ADRs (if architecture decision was made)
- Create new ADR in `docs/adr/`
- Update `docs/adr/README.md` index

### Step 3: Stage and Commit

```bash
git add .
git commit -m "Descriptive commit message"
```

**Commit Message Guidelines:**
- Use English
- Be descriptive but concise
- Examples:
  - `Add tag management feature`
  - `Fix image analysis for large files`
  - `Update API documentation`

### Step 4: Confirm

Show the user:
- What was committed
- Current git status

## Language Policy

**All documentation and comments must be in English:**
- README.md, CONTEXT.md, AGENTS.md
- All ADR files in `docs/adr/`
- Code comments (JavaDoc, inline)
- Commit messages
- OpenAPI descriptions

## Checklist

Before committing, verify:

- [ ] Code compiles/works
- [ ] Comments are in English
- [ ] README updated (if new feature)
- [ ] CONTEXT.md updated (if structure changed)
- [ ] ADR created (if architecture decision)
- [ ] Commit message is descriptive

## Reference Files

- **README:** `README.md`
- **Context:** `CONTEXT.md`
- **Agent Guidelines:** `AGENTS.md`
- **ADR Index:** `docs/adr/README.md`
- **Java Sources:** `src/main/java/com/mvolkert/note2mermaid/`
