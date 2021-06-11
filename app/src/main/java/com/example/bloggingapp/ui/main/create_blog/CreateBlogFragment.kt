package com.example.bloggingapp.ui.main.create_blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import com.example.bloggingapp.R
import com.example.bloggingapp.ui.*
import com.example.bloggingapp.ui.main.create_blog.state.Create_Blog_State_Event
import com.example.bloggingapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_MUST_SELECT_IMAGE
import com.example.bloggingapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.example.bloggingapp.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CreateBlogFragment : BaseCreateBlogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setHasOptionsMenu(true)






        blog_image.setOnClickListener{
            if(stateChangeListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }
        update_textview.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
            subscribeObserver()
    }


    private fun subscribeObserver(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{
            dataState->
            stateChangeListener.onDataStateChanged(dataState)

            dataState.data?.let{
                data ->
                data.response?.let{
                    event ->
                    event.peekContent().let{
                        response ->
                        response.message?.let{
                            message->
                            if(message.equals(SUCCESS_BLOG_CREATED)){
                                viewModel.clearNewBlogFeilds()
                            }
                        }
                    }
                }
            }






        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            viewState->
            viewState.blogFeilds.let{
                newBlogFields ->

                setBlogProperties(
                    newBlogFields.newBlogTitle,
                            newBlogFields.newBlogBody,
                            newBlogFields.newImageUri
                )


            }
        })

    }


    private fun setBlogProperties(title:String?, body:String?, image: Uri?){
        image?.let{
                requestManager.load(image)
                    .into(blog_image)
        }?:setDefaultImage()


        blog_title.setText(title)
        blog_body.setText(body)


    }


    private fun setDefaultImage(){
        requestManager.load(R.drawable.default_image)
            .into(blog_image)
    }












    private fun pickFromGallery() {
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type="image/*"
        val mimeType= arrayOf("image/jpeg", "image/png", "image/jgp")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent,GALLERY_REQUEST_CODE)
    }


    private fun showErrorDialog(errorMessage:String){
        stateChangeListener.onDataStateChanged(




            DataState(
                Event(StateError(Response(
                 errorMessage,ResponseType.Dialog()
                ))),
                Loading(isLoading=false),
                Data(Event.dataEvent(null),null)
            )

        )
    }


private fun launchImageCrop(uri: Uri?){  //This only for cropping inside the fragment
    context?.let{
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(it,this)

    }
}



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            when(requestCode){
                GALLERY_REQUEST_CODE->{
                    //This means that the user have attached a image and now we hae to launch a image cropper
                    data?.data?.let{
                        uri->
                        launchImageCrop(uri)
                    }
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE->{
                        var result=CropImage.getActivityResult(data)
                    val resultUri=result.uri
                    viewModel.setNewBlogFeilds(
                        title=null,
                        body=null,
                        uri=resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE->{
                    showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

            }
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFeilds(
            blog_title.text.toString(),
            blog_body.text.toString(),
            null

        )
    }


    private fun publishNewBlog(){
        var multipartBody:MultipartBody.Part?=null
        viewModel.viewState.value?.blogFeilds?.newImageUri?.let{
            imageUri->
            imageUri.path?.let{
                filePath->
                val imageFile= File(filePath)
                Log.d(TAG, "CreateBlogFragement: imageFile: ${imageFile} ")
                val requestBody= RequestBody.create(
                    MediaType.parse("image/*"),
                    imageFile
                )

                multipartBody=MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )

            }
        }

        multipartBody?.let{
            viewModel.setStateEvent(
                Create_Blog_State_Event.CreateNewBlogEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    it
                )
            )
            stateChangeListener.hideSoftkeyboard()


        }?:showErrorDialog(ERROR_MUST_SELECT_IMAGE)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.publish_menu,menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.publish->{
                val callback:AreYouSureCallback=object:AreYouSureCallback{
                    override fun proceed() {
                        publishNewBlog()
                    }

                    override fun cancel() {
                        //ignore
                     }

                }
                    uiCommunicationListener.onUiMessageReceived(
                        UIMessage(
                            getString(R.string.are_you_sure_publish),
                            UIMessageType.AreYouSureDialog(callback )
                        )
                    )
                return true

            }
        }


        return super.onOptionsItemSelected(item)
    }




}