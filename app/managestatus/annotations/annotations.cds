using szakdolgozat.srv.service.StatusService as service from '../../../srv/services/StatusService';

annotate service.Status with @(

    //enable the create, edit button and delete button on the list report page
    odata.draft.enabled,
    UI.UpdateHidden                          : false,
    Capabilities.DeleteRestrictions.Deletable: true,
    Capabilities.UpdateRestrictions.Updatable: true,


    //List report page - Fields that are displayed by default
    UI.LineItem                              : [
        {
            $Type: 'UI.DataField',
            Value: name
        },
        {
            $Type: 'UI.DataField',
            Value: isFinalStatus
        }
    ],

    //Object Page - Section Definitions
    UI.Facets                                : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Project Status Properties',
            ID    : 'ProjectStatusProperties',
            Target: '@UI.FieldGroup#ProjectStatusProperties'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Administration',
            ID    : 'Administration',
            Target: '@UI.FieldGroup#Administration'
        }
    ],

    //Object Page - Header
    UI.HeaderInfo                            : {
        Title         : {
            $Type: 'UI.DataField',
            Value: 'Project Status'
        },
        TypeName      : 'Project Status',
        TypeNamePlural: 'Project Statuses'
    },

    //Object Page - Project Status Properties - Field Definitions
    UI.FieldGroup #ProjectStatusProperties   : {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: name
            },
            {
                $Type: 'UI.DataField',
                Value: isFinalStatus
            }
        ]
    },
    //Object Page - Administration - Field Definitions
    UI.FieldGroup #Administration            : {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: createdAt
            },
            {
                $Type: 'UI.DataField',
                Value: createdBy
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedAt
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedBy
            }
        ]
    },


);
