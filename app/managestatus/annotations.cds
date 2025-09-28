using szakdolgozat.srv.service.StatusService as service from '../../srv/services/StatusService';
annotate service.Status with @(
    UI.FieldGroup #GeneratedGroup : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'name',
                Value : name,
            },
            {
                $Type : 'UI.DataField',
                Label : 'isFinalStatus',
                Value : isFinalStatus,
            },
            {
                $Type : 'UI.DataField',
                Label : 'delete_ac',
                Value : delete_ac,
            },
        ],
    },
    UI.Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            ID : 'GeneratedFacet1',
            Label : 'General Information',
            Target : '@UI.FieldGroup#GeneratedGroup',
        },
    ],
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'name',
            Value : name,
        },
        {
            $Type : 'UI.DataField',
            Label : 'isFinalStatus',
            Value : isFinalStatus,
        },
        {
            $Type : 'UI.DataField',
            Label : 'delete_ac',
            Value : delete_ac,
        },
    ],
);

