package curso.android.seccion_04_realm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import curso.android.seccion_04_realm.R;
import curso.android.seccion_04_realm.models.Note;

public class NoteAdapter extends BaseAdapter {

    private List<Note> notes;
    private int layout;
    private Context context;

    public NoteAdapter(List<Note> notes, int layout, Context context) {
        this.notes = notes;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int i) {
        return notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout,null);
            vh = new ViewHolder();
            vh.textViewDescription = convertView.findViewById(R.id.textViewNoteDescription);
            vh.textViewCreatedAt = convertView.findViewById(R.id.textViewNoteCreatedAt);
            convertView.setTag(vh);
        }else vh = (ViewHolder) convertView.getTag();

        Note note = notes.get(position);

        vh.textViewDescription.setText(note.getDescription());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String date = df.format(note.getCreatedAt());
        vh.textViewCreatedAt.setText(date);

        return convertView;
    }

    public class ViewHolder{
        private TextView textViewDescription;
        private TextView textViewCreatedAt;
    }
}
