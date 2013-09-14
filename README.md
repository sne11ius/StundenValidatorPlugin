StundenValidatorPlugin
======================

Plugin for https://github.com/sne11ius/stunden that validates input data.

The plugin is supposed to make sure
 - Work period must not be empty
 - Work period must start on monday or the first day of month
 - Work period must end on (friday, saturday, sunday) or on last day of month
 - Work period must contain >= 5 days
 - Work period must not contain holes - saturday and sunday are optional
 - Each day must contain at least 1 entry
 - Entries for a day must not overlap or have holes
 - Entries must have a non-empty project name

Configuration
=============

There currently is no way of configuring this plugin. It will eather be happy or throw an Exception.

Build
=====
see https://github.com/sne11ius/stunden
