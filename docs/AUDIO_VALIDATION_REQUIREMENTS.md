# AUDIO_VALIDATION_REQUIREMENTS.md

## REQUIRED AUDIO VALIDATION

Validator must verify:
- file exists
- file decodes successfully
- duration > 0
- MIME valid
- path unique
- ID unique
- category valid
- catalog synchronized

## REQUIRED REPORTING

Validator output must include:
- playable count
- failed decode count
- uncataloged assets
- duplicate IDs
- duplicate paths
- duplicate content hashes
- unsupported files
- missing assets

## INVALID VALIDATION

The following is NOT sufficient:
path exists == valid
