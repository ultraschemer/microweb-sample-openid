"""Creating document and image relations.

Revision ID: f020117fa010
Revises: 58a0c70dd98a
Create Date: 2020-06-25 10:12:08.692395

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'f020117fa010'
down_revision = '58a0c70dd98a'
branch_labels = None
depends_on = None


def upgrade():
    # Create the table 'image' only with data fields. The boilerplate fields (like id, created_at, etc)
    # will be created later
    op.create_table('image',
                    # This is the image name - just a helper. No uniqueness ensured:
                    sa.Column('name', sa.String(1024), nullable=False),
                    # The image data. Since it's saved in database, and it's binary data, we store it as
                    # Base64 information. This is a non-optimized option, just to ensure simplicity in this
                    # sample:
                    sa.Column('base64data', sa.Text(), nullable=False))

    # There are other three set-type methods of op: set_identifiable, se_createable and set timeable.
    # They configure suitably the tables on which they're applied, so they can be mapped as Tdentiable, Createable
    #  and Timeable entities correctly.

    # The 'image' relation store read-only registers. These registers are of type Createable. So, set the table as
    # a 'createable' entity - and this function will generate all boilerplate fields to fulfill the needs of a
    # 'createable' entity.
    op.set_createable('image')

    # Table references in Microweb are built in a default fashion, using the method 'op.set_reference'.
    # Let's create a reference to a user, which will be the image owner. Is it possible to have an image
    # without a owner? Yes, it is. A system image, for example. So, this reference is nullable:
    op.set_reference(
        # this is the table which owns the reference, i.e, the table which has the foreign key:
        'image',
        # This is the Foreign key field name, present on 'image' table, which has the foreign key:
        'owner_user_id',
        # This is the table receiving the reference, i.e, the table pointed by the foreign key, on it's 'id' field:
        'user_',
        # This user_id reference field is nullable, so we set the 'rtn' field, to reach the 'n' field.
        # The 'rtn' field is the reference table field name, which, by default equals to 'id'. This single lettered
        # parameter names is to remember the user that these fields are optional, non-default, and must be used only
        # in very well thought structures:
        'id',
        # This user_id reference field is nullable, so the 'n' (from nullable) parameter is set:
        True)

    # Create the relationship table between user and image - the table need to have, at least a column:
    op.create_table('user__image',
                    # This is a pure relationship table, but alembic/database limits the creation of empty tables,
                    # so we can create a dummy field. To avoid creating dummy fields, and customizing the relationship
                    # table in a useful way, we create a relationship table between a user and an image, assigning an
                    # alias field - so the image can appear to the user with a different name from its owner:
                    sa.Column('alias', sa.String(1024), nullable=True))
    # The relationship, originally was imagined as a Createable, but since it has an alias, which can be updated, then
    # it's a Timeable:
    op.set_timeable('user__image')

    # Set the references:

    # Use the default optional parameters - since this field is not nullable, and the reference
    # primary key is default, too:
    op.set_reference('user__image', 'image_id', 'image')
    op.set_reference('user__image', 'user_id', 'user_')

    # Since the references are set in the relationship table, ensure unicity - the unique key has a name, to ensure
    # it is accessible in the future, for updates or changes:
    op.create_unique_constraint('ui_65e8sZZ5_uidx', 'user__image', ['image_id', 'user_id'])

    # Create the text document entity:
    op.create_table('document',
                    sa.Column('name', sa.String(1024), nullable=False),
                    sa.Column('contents', sa.Text(), nullable=False),
                    # A status for document. 'regular' is de default document status, and its default format too.
                    # Other status can be set in the future.
                    sa.Column('status', sa.String(16), nullable=False, server_default='regular'))

    # Documents are quintessentially Timeable:
    op.set_timeable('document')

    # As an 'image', a 'document' has an optional owner:
    op.set_reference('document', 'owner_user_id', 'user_', 'id', True)

    # The document/user relationship is similar to the image/user relationship:
    op.create_table('user__document', sa.Column('alias', sa.String(1024), nullable=True))
    op.set_timeable('user__document')

    op.set_reference('user__document', 'document_id', 'document')
    op.set_reference('user__document', 'user_id', 'user_')


def downgrade():
    op.drop_table('user__document')
    op.drop_table('document')
    op.drop_table('user__image')
    op.drop_table('image')
